package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TestCase extends AbstractEntity {
    private static final long serialVersionUID = 4877029098773384360L;

    private String testClass;
    private String testMethod;
    private Status status;
    private String info;
    private Long testSuiteId;
    private User primaryOwner = new User();
    private User secondaryOwner = new User();
    private TestSuite testSuite = new TestSuite();
    private Project project;
    private Long stability;

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TestCase && this.hashCode() == obj.hashCode());
    }

    @Override
    public int hashCode() {
        return (testClass + testMethod + testSuiteId + info +
                primaryOwner.getId() + secondaryOwner.getId() + (project != null ? project.getName() : "")).hashCode();
    }
}
