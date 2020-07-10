package com.zebrunner.reporting.web.request.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.googlecode.jmapper.annotations.JGlobalMap;
import com.zebrunner.reporting.web.util.serializer.ToJsonDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@JGlobalMap
@NoArgsConstructor
public class TestSessionStartRequest {

    @NotEmpty
    private String sessionId;

    @NotNull
    @PastOrPresent
    private OffsetDateTime startedAt;

    @JsonDeserialize(using = ToJsonDeserializer.class)
    private String desiredCapabilities;

    @JsonDeserialize(using = ToJsonDeserializer.class)
    private String capabilities;

    private Set<Long> testRefs = new HashSet<>();

}
