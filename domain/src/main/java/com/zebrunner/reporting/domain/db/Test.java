package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class Test extends AbstractEntity implements Comparable<Test> {
    private static final long serialVersionUID = -915700504693067056L;

    private String name;
    private Status status;
    private String testArgs;
    private Long testRunId;
    private Long testCaseId;
    private String testGroup;
    private String message;
    private Integer messageHashCode;
    private Date startTime;
    private Date finishTime;
    private int retry;
    private TestConfig testConfig;
    private List<WorkItem> workItems;
    private boolean knownIssue;
    private boolean blocker;
    private boolean needRerun;
    private String owner;
    private String secondaryOwner;
    private String dependsOnMethods;
    private String testClass;
    private Set<TestArtifact> artifacts = new HashSet<>();
    private String ciTestId;
    private Set<Tag> tags;

    public Test() {
        this.testConfig = new TestConfig();
    }

    public String getNotNullTestGroup() {
        return testGroup == null ? "n/a" : testGroup;
    }

    public WorkItem getWorkItemByType(WorkItem.Type type) {
        return workItems.stream()
                        .filter(workItem -> type.equals(workItem.getType()))
                        .findFirst()
                        .orElse(null);
    }

    @Override
    public int compareTo(Test test) {
        if (Arrays.asList(Status.QUEUED, Status.ABORTED, Status.SKIPPED, Status.FAILED).contains(this.getStatus())) {
            return -1;
        } else {
            return 0;
        }
    }
}