package com.zebrunner.reporting.web.response.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.jmapper.annotations.JGlobalMap;
import com.zebrunner.reporting.web.util.deserializer.FromJsonSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@JGlobalMap
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestSessionSaveResponse {

    private Long id;
    private String sessionId;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    @JsonSerialize(using = FromJsonSerializer.class)
    private String desiredCapabilities;
    @JsonSerialize(using = FromJsonSerializer.class)
    private String capabilities;
    private Set<Long> testRefs = new HashSet<>();

}
