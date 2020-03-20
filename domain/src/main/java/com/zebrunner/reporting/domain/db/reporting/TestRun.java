package com.zebrunner.reporting.domain.db.reporting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TestRun {

    private Long id;
    private String name;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String framework;
    private String config;
    private LaunchContext launchContext;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LaunchContext {

        private String jobNumber;
        private String upstreamJobNumber;
    }
}
