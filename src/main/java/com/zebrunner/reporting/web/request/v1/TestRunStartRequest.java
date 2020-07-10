package com.zebrunner.reporting.web.request.v1;

import com.googlecode.jmapper.annotations.JGlobalMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.zebrunner.reporting.domain.db.TestConfig;
import com.zebrunner.reporting.domain.db.reporting.TestRun;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@JGlobalMap
@NoArgsConstructor
public class TestRunStartRequest {

    private String uuid;

    @NotBlank
    private String name;

    @NotNull
    @PastOrPresent
    private OffsetDateTime startedAt;

    @NotBlank
    private String framework;

    private TestConfig config;

    @Valid
    private LaunchContext launchContext;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LaunchContext {

        @NotBlank
        private String jobNumber;

        @Size(min = 1)
        private String upstreamJobNumber;

    }

    @JMapConversion(from = "launchContext", to = "launchContext")
    public TestRun.LaunchContext convertLaunchContext(LaunchContext launchContext) {
        return new TestRun.LaunchContext(launchContext.getJobNumber(), launchContext.getUpstreamJobNumber());
    }

}
