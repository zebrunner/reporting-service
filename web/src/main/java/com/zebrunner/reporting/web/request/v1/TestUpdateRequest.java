package com.zebrunner.reporting.web.request.v1;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@JGlobalMap
@NoArgsConstructor
public class TestUpdateRequest {

    private String uuid;
    private String name;
    private String className;
    private String methodName;
    private String maintainer;
    private String testCase;
    private List<String> tags;
    private Map<String, String> additionalAttributes;

    private OffsetDateTime endedAt;
    private String result;
    private String reason;

}
