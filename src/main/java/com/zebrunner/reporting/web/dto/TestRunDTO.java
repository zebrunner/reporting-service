package com.zebrunner.reporting.web.dto;

import com.zebrunner.reporting.domain.db.TestConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TestRunDTO {

    @Positive
    private Long id;

    private String uuid;

    @NotBlank(groups = ValidationGroups.TestRunStartGroup.class)
    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    private String name;

    @PastOrPresent(groups = ValidationGroups.TestRunStartGroup.class)
    @NotNull(groups = ValidationGroups.TestRunStartGroup.class)
    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    private OffsetDateTime startedAt;

    @Null(groups = ValidationGroups.TestRunStartGroup.class)
    @NotNull(groups = ValidationGroups.TestRunFinishGroup.class)
    @PastOrPresent(groups = ValidationGroups.TestRunFinishGroup.class)
    private OffsetDateTime endedAt;

    @NotBlank(groups = ValidationGroups.TestRunStartGroup.class)
    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    private String framework;

    @Null(groups = ValidationGroups.TestRunFinishGroup.class)
    private TestConfig config;

    @Valid
    private LaunchContextDTO launchContext;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LaunchContextDTO {

        @NotNull(groups = ValidationGroups.TestRunStartGroup.class)
        private String jobNumber;

        @NotNull(groups = ValidationGroups.TestRunStartGroup.class)
        private String upstreamJobNumber;

    }

    public static class ValidationGroups {
        public interface AllGroups {
        }

        public interface TestRunStartGroup extends AllGroups {
        }

        public interface TestRunFinishGroup extends AllGroups {
        }
    }

}
