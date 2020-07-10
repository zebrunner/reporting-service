package com.zebrunner.reporting.web.response.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.googlecode.jmapper.annotations.JGlobalMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.zebrunner.reporting.domain.db.TestConfig;
import com.zebrunner.reporting.domain.db.reporting.TestRun;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.OffsetDateTime;

@Data
@JGlobalMap
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestRunSaveResponse {

    private Long id;
    private String uuid;
    private String name;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private String framework;
    private TestConfig config;
    private LaunchContext launchContext;

    @Value
    public static class LaunchContext {

        String jobNumber;
        String upstreamJobNumber;

    }

    @JMapConversion(from = "launchContext", to = "launchContext")
    public LaunchContext convertLaunchContext(TestRun.LaunchContext launchContext) {
        return new LaunchContext(launchContext.getJobNumber(), launchContext.getUpstreamJobNumber());
    }

}
