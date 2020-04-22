package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.TestConfigMapper;
import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.TestConfig;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.db.config.Argument;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_RUN_NOT_FOUND;
import static com.zebrunner.reporting.service.util.XmlConfigurationUtil.readArguments;

@Service
public class TestConfigService {

    private static final String ERR_MSG_TEST_RUN_NOT_FOUND = "Test run with id %s can not be found";

    @Autowired
    private TestConfigMapper testConfigMapper;

    @Autowired
    private TestRunService testRunService;

    @Transactional
    public void createTestConfig(TestConfig testConfig) {
        testConfigMapper.createTestConfig(testConfig);
    }

    @Transactional
    public TestConfig createTestConfigForTest(Test test, String testConfigXML) {
        Long testRunId = test.getTestRunId();
        TestRun testRun = testRunService.getTestRunById(testRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, testRunId);
        }

        List<Argument> testRunConfig = readArguments(testRun.getConfigXML()).getArg();
        List<Argument> testConfig = readArguments(testConfigXML).getArg();

        TestConfig config = new TestConfig()
                .init(testRunConfig)
                .init(testConfig);

        TestConfig existingTestConfig = searchTestConfig(config);
        if (existingTestConfig != null) {
            config = existingTestConfig;
        } else {
            createTestConfig(config);
        }

        return config;
    }

    @Transactional
    public TestConfig createTestConfigForTestRun(String configXML) {
        List<Argument> testRunConfig = readArguments(configXML).getArg();

        TestConfig config = new TestConfig().init(testRunConfig);

        TestConfig existingTestConfig = searchTestConfig(config);
        if (existingTestConfig != null) {
            config = existingTestConfig;
        } else {
            createTestConfig(config);
        }
        return config;
    }

    @Transactional(readOnly = true)
    public List<String> getPlatforms() {
        return testConfigMapper.getPlatforms();
    }

    @Transactional(readOnly = true)
    public List<String> getBrowsers() {
        return testConfigMapper.getBrowsers();
    }

    @Transactional(readOnly = true)
    public List<String> getLocales() {
        return testConfigMapper.getLocales();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "environments", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #result", condition = "#result != null && #result.size() != 0")
    public List<String> getEnvironments() {
        return testConfigMapper.getEnvironments();
    }

    @Transactional(readOnly = true)
    public TestConfig searchTestConfig(TestConfig testConfig) {
        return testConfigMapper.searchTestConfig(testConfig);
    }
}
