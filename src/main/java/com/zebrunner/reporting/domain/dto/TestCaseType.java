package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestCaseType extends AbstractType {

    private static final long serialVersionUID = 4361075320159665047L;

    @NotNull
    private String testClass;
    @NotNull
    private String testMethod;
    private String info;
    @NotNull
    private Long testSuiteId;
    @NotNull
    private Long primaryOwnerId;
    private Long secondaryOwnerId;
    private ProjectDTO project;

    public TestCaseType(String testClass, String testMethod, String info, Long testSuiteId, Long primaryOwnerId) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.info = info;
        this.testSuiteId = testSuiteId;
        this.primaryOwnerId = primaryOwnerId;
    }

    public TestCaseType(String testClass, String testMethod, String info, Long testSuiteId, Long primaryOwnerId, Long secondaryUserId) {
        this(testClass, testMethod, info, testSuiteId, primaryOwnerId);
        this.secondaryOwnerId = secondaryUserId;
    }

}
