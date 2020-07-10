package com.zebrunner.reporting.web.response.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@JGlobalMap
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TestSaveResponse {

    private Long id;
    private String uuid;
    private String name;
    private String className;
    private String methodName;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private String maintainer;
    private String testCase;
    private List<String> tags;
    private Map<String, String> additionalAttributes;
    private String result;
    private String reason;

}
