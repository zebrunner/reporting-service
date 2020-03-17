package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.TestRun;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestRunType extends AbstractType {
    private static final long serialVersionUID = -1687311347861782118L;
    private String ciRunId;
    @NotNull
    private Long testSuiteId;
    private Status status;
    private String scmURL;
    private String scmBranch;
    private String scmCommit;
    private String configXML;
    @NotNull
    private Long jobId;
    private Long upstreamJobId;
    private Integer upstreamJobBuildNumber;
    @NotNull
    private Integer buildNumber;
    @NotNull
    private TestRun.Initiator startedBy;
    private Long userId;
    private String workItem;
    private ProjectDTO project;
    private boolean knownIssue;
    private boolean blocker;
    private boolean reviewed;

    public TestRunType(String ciRunId, Long testSuiteId, Long userId, String scmURL, String scmBranch, String scmCommit,
                       String configXML, Long jobId, Integer buildNumber, TestRun.Initiator startedBy, String workItem) {
        this.ciRunId = ciRunId;
        this.testSuiteId = testSuiteId;
        this.userId = userId;
        this.scmURL = scmURL;
        this.scmBranch = scmBranch;
        this.scmCommit = scmCommit;
        this.configXML = configXML;
        this.jobId = jobId;
        this.buildNumber = buildNumber;
        this.startedBy = startedBy;
        this.workItem = workItem;
    }

    public TestRunType(String ciRunId, Long testSuiteId, String scmURL, String scmBranch, String scmCommit,
                       String configXML, Long jobId, Long upstreamJobId, Integer upstreamJobBuildNumber, Integer buildNumber,
                       TestRun.Initiator startedBy, String workItem) {
        this.ciRunId = ciRunId;
        this.testSuiteId = testSuiteId;
        this.scmURL = scmURL;
        this.scmBranch = scmBranch;
        this.scmCommit = scmCommit;
        this.configXML = configXML;
        this.jobId = jobId;
        this.upstreamJobId = upstreamJobId;
        this.upstreamJobBuildNumber = upstreamJobBuildNumber;
        this.buildNumber = buildNumber;
        this.startedBy = startedBy;
        this.workItem = workItem;
    }

    public TestRunType(String ciRunId, Long testSuiteId, String scmURL, String scmBranch, String scmCommit,
                       String configXML, Long jobId, Integer buildNumber, TestRun.Initiator startedBy, String workItem) {
        this.ciRunId = ciRunId;
        this.testSuiteId = testSuiteId;
        this.scmURL = scmURL;
        this.scmBranch = scmBranch;
        this.scmCommit = scmCommit;
        this.configXML = configXML;
        this.jobId = jobId;
        this.buildNumber = buildNumber;
        this.startedBy = startedBy;
        this.workItem = workItem;
    }

}