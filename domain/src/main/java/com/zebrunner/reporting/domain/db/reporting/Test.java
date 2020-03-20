package com.zebrunner.reporting.domain.db.reporting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Test {

    private Long id;
    private String name;
    private String className;
    private String methodName;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String maintainer;
    private String testCase;
    private List<String> tags;
    private Map<String, String> additionalAttributes;
    private String result;
    private String reason;
}
