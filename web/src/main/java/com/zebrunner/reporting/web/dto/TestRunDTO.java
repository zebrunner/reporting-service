package com.zebrunner.reporting.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TestRunDTO {

    @Null(groups = ValidationGroups.AllGroups.class)
    private Long id;

    @NotBlank(groups = ValidationGroups.TestRunStartGroup.class)
    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    private String name;

    @PastOrPresent(groups = ValidationGroups.TestRunStartGroup.class)
    @NotNull(groups = ValidationGroups.TestRunStartGroup.class)
    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    private LocalDateTime startedAt;

    @Null(groups = ValidationGroups.TestRunStartGroup.class)
    @NotNull(groups = ValidationGroups.TestRunFinishGroup.class)
    @PastOrPresent(groups = ValidationGroups.TestRunFinishGroup.class)
    private LocalDateTime endedAt;

    @NotBlank(groups = ValidationGroups.TestRunStartGroup.class)
    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    private String framework;

    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    private String config;

    @NotNull(groups = ValidationGroups.TestRunStartGroup.class)
    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    @Valid
    private LaunchContext launchContext;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LaunchContext {

        @Positive(groups = ValidationGroups.TestRunStartGroup.class)
        @NotNull(groups = ValidationGroups.TestRunStartGroup.class)
        private String jobNumber;

        @Positive(groups = ValidationGroups.TestRunStartGroup.class)
        @NotNull(groups = ValidationGroups.TestRunStartGroup.class)
        private String upstreamJobNumber;
    }

    public static class ValidationGroups {
        public interface AllGroups {}
        public interface TestRunStartGroup extends AllGroups {}
        public interface TestRunFinishGroup extends AllGroups {}
    }

}
