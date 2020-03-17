package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.TagMapper;
import com.zebrunner.reporting.domain.db.Tag;
import com.zebrunner.reporting.domain.db.TagIntegrationData;
import com.zebrunner.reporting.domain.db.TestInfo;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.db.config.Configuration;
import com.zebrunner.reporting.domain.dto.tag.IntegrationTag;
import com.zebrunner.reporting.service.util.URLResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zebrunner.reporting.service.util.XmlConfigurationUtil.readArguments;

@Service
public class TagService {

    private final TagMapper tagMapper;
    private final TestRunService testRunService;
    private final URLResolver urlResolver;

    public TagService(TagMapper tagMapper, TestRunService testRunService, URLResolver urlResolver) {
        this.tagMapper = tagMapper;
        this.testRunService = testRunService;
        this.urlResolver = urlResolver;
    }

    @Transactional(rollbackFor = Exception.class)
    public Tag createTag(Tag tag) {
        tagMapper.createTag(tag);
        if (tag.getId() == null || tag.getId() == 0) {
            Tag existingTag = getTagByNameAndValue(tag.getName(), tag.getValue());
            if (existingTag != null) {
                tag = existingTag;
            }
        }
        return tag;
    }

    @Transactional(rollbackFor = Exception.class)
    public Set<Tag> createTags(Set<Tag> tags) {
        Set<Tag> result = new HashSet<>();
        if (tags != null && !tags.isEmpty()) {
            result = tags.stream().map(this::createTag).collect(Collectors.toSet());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<TestInfo> getTestInfoByTagNameAndTestRunCiRunId(IntegrationTag tagName, String ciRunId) {
        return tagMapper.getTestInfoByTagNameAndTestRunCiRunId(tagName, ciRunId);
    }

    @Transactional(readOnly = true)
    public Tag getTagByNameAndValue(String name, String value) {
        return tagMapper.getTagByNameAndValue(name, value);
    }

    @Transactional(readOnly = true)
    public TagIntegrationData exportTagIntegrationData(String ciRunId, IntegrationTag tagName) {
        TestRun testRun = testRunService.getTestRunByCiRunIdFull(ciRunId);
        // ConfigXML parsing for TestRunName generation
        Configuration configuration = readArguments(testRun.getConfigXML());
        return TagIntegrationData.builder()
                                 .testRunName(testRun.getName())
                                 .testInfo(getTestInfoByTagNameAndTestRunCiRunId(tagName, ciRunId))
                                 .finishedAt(getFinishedAt(testRun))
                                 .startedAt(testRun.getStartedAt())
                                 .createdAfter(testRun.getCreatedAt())
                                 .env(testRun.getConfig().getEnv())
                                 .testRunId(testRun.getId().toString())
                                 .zafiraServiceUrl(urlResolver.buildWebURL())
                                 .customParams(getCustomParams(tagName, configuration))
                                 .build();
    }

    private Map<String, String> getCustomParams(IntegrationTag tagName, Configuration configuration) {
        Map<String, String> customParams = new HashMap<>();
        // IntegrationType-specific properties adding
        switch (tagName) {
            case TESTRAIL_TESTCASE_UUID:
                configuration.getArg().forEach(arg -> {
                    String key = arg.getKey();
                    String value = arg.getValue();
                    if (key.contains("testrail_assignee")) {
                        customParams.put("assignee", value);
                    } else if (key.contains("testrail_milestone")) {
                        customParams.put("milestone", value);
                    } else if (key.contains("testrail_run_name")) {
                        customParams.put("testrail_run_name", value);
                    }
                });
                break;
            case QTEST_TESTCASE_UUID:
                configuration.getArg().forEach(arg -> {
                    String key = arg.getKey();
                    String value = arg.getValue();
                    if (key.contains("qtest_cycle_name")) {
                        customParams.put("cycle_name", value);
                    }
                });
        }
        return customParams;
    }

    private Date getFinishedAt(TestRun testRun) {
        // finishedAt value generation based on startedAt & elapsed
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testRun.getStartedAt());
        if (testRun.getElapsed() != null) {
            calendar.add(Calendar.SECOND, testRun.getElapsed());
        }
        return calendar.getTime();
    }

}
