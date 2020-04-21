package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.StringUtils.isEmpty;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestRun extends AbstractEntity {
    private static final long serialVersionUID = -1847933012610222160L;

    private Map<String, String> configuration = new HashMap<>();
    private String ciRunId;
    private User user;
    private TestSuite testSuite;
    private Status status;
    private String scmURL;
    private String scmBranch;
    private String scmCommit;
    @JsonIgnore
    private String configXML;
    private WorkItem workItem;
    private Job job;
    private Integer buildNumber;
    private Job upstreamJob;
    private Integer upstreamJobBuildNumber;
    private Initiator startedBy;
    private Project project;
    private boolean knownIssue;
    private boolean blocker;
    private Date startedAt;
    private Integer elapsed;
    private Integer eta;
    private String comments;
    private String channels;
    private TestConfig config;

    private Integer passed;
    private Integer failed;
    private Integer failedAsKnown;
    private Integer failedAsBlocker;
    private Integer skipped;
    private Integer inProgress;
    private Integer aborted;
    private Integer queued;
    private boolean reviewed;

    private Set<TestRunArtifact> artifacts = new HashSet<>();

    @Builder
    public TestRun(Long id, String ciRunId) {
        super(id);
        this.ciRunId = ciRunId;
    }

    public String getName() {
        // For most cases config is present, but for a small amount of
        // invalid data we should process this case
        if (config == null) {
            return "";
        }
        String name = "%s %s (%s) on %s %s";
        String appVersion = !isEmpty(config.getAppVersion()) ? config.getAppVersion() + " - " : "";
        String platformInfo = buildPlatformInfo();
        return String.format(name, appVersion, testSuite.getName(), testSuite.getFileName(), config.getEnv(), platformInfo).trim();
    }

    private String buildPlatformInfo() {
        StringBuilder platformInfoBuilder = new StringBuilder();
        platformInfoBuilder.append(config.buildPlatformName());
        if (!"en_US".equals(config.getLocale())) {
            platformInfoBuilder.append(" ")
                               .append(config.getLocale());
        }
        platformInfoBuilder.insert(0, "(");
        platformInfoBuilder.append(")");
        return platformInfoBuilder.toString();
    }

    public enum Initiator {
        SCHEDULER,
        UPSTREAM_JOB,
        HUMAN
    }

}
