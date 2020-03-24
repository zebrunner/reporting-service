package com.zebrunner.reporting.domain.db.reporting;

import com.zebrunner.reporting.domain.db.TestConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TestRun {

    private Long id;
    private String uuid;
    private String name;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private String framework;
    private TestConfig config;
    private LaunchContext launchContext;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LaunchContext {

        private String jobNumber;
        private String upstreamJobNumber;
    }
}
