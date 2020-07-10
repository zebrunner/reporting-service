package com.zebrunner.reporting.web.request.v1;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@JGlobalMap
@NoArgsConstructor
public class TestStartRequest {

    @NotBlank
    private String uuid;

    @NotBlank
    private String name;

    @NotBlank
    private String className;

    @NotBlank
    private String methodName;

    @NotNull
    @PastOrPresent
    private OffsetDateTime startedAt;

    private String maintainer;
    private String testCase;
    private List<String> tags;
    private Map<String, String> additionalAttributes;

}
