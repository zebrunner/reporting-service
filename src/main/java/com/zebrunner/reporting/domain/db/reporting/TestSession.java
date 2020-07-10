package com.zebrunner.reporting.domain.db.reporting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class TestSession {

    private Long id;
    private String sessionId;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private String desiredCapabilities;
    private String capabilities;
    private Set<Long> testRefs;

}
