package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestType extends AbstractType {
    private static final long serialVersionUID = 7777895715362820880L;
    @NotNull
    private String name;
    private Status status;
    private String testArgs;
    @NotNull
    private Long testRunId;
    @NotNull
    private Long testCaseId;
    private String testGroup;
    private String message;
    private Integer messageHashCode;
    private Long startTime;
    private Long finishTime;
    private List<String> workItems;
    private int retry;
    private String configXML;
    private Map<String, Long> testMetrics;
    private boolean knownIssue;
    private boolean blocker;
    private boolean needRerun;
    private String dependsOnMethods;
    private String testClass;
    @Valid
    private Set<TestArtifactType> artifacts = new HashSet<>();
    private String ciTestId;
    @Valid
    private Set<TagType> tags;

    public TestType(String name, Status status, String testArgs, Long testRunId, Long testCaseId, Long startTime,
            List<String> workItems, int retry, String configXML) {
        this.name = name;
        this.status = status;
        this.testArgs = testArgs;
        this.testRunId = testRunId;
        this.testCaseId = testCaseId;
        this.startTime = startTime;
        this.workItems = workItems;
        this.retry = retry;
        this.configXML = configXML;
    }

}