package com.zebrunner.reporting.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TestDTO {

    @Positive
    private Long id;

    @NotBlank(groups = ValidationGroups.TestStartGroup.class)
    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private String uuid;

    @NotBlank(groups = ValidationGroups.TestStartGroup.class)
    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private String name;

    @NotBlank(groups = ValidationGroups.TestStartGroup.class)
    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private String className;

    @NotBlank(groups = ValidationGroups.TestStartGroup.class)
    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private String methodName;

    @PastOrPresent(groups = ValidationGroups.TestStartGroup.class)
    @NotNull(groups = {ValidationGroups.TestStartGroup.class, ValidationGroups.HeadlessTestStartGroup.class})
    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private OffsetDateTime startedAt;

    @PastOrPresent(groups = ValidationGroups.TestFinishGroup.class)
    @NotNull(groups = ValidationGroups.TestFinishGroup.class)
    @Null(groups = ValidationGroups.TestStartGroup.class)
    private OffsetDateTime endedAt;

    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private String maintainer;

    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private String testCase;

    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private List<String> tags;

    @Null(groups = ValidationGroups.TestFinishGroup.class)
    private Map<String, String> additionalAttributes;

    @Null(groups = ValidationGroups.TestStartGroup.class)
    @NotBlank(groups = ValidationGroups.TestFinishGroup.class)
    private String result;

    @Null(groups = ValidationGroups.TestStartGroup.class)
    private String reason;

    public static class ValidationGroups {
        public interface AllGroups {}
        public interface TestStartGroup extends AllGroups {}
        public interface HeadlessTestStartGroup extends AllGroups {}
        public interface TestFinishGroup extends AllGroups {}
    }
}
