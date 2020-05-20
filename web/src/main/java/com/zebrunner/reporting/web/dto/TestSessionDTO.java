package com.zebrunner.reporting.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zebrunner.reporting.web.util.deserializer.FromJsonSerializer;
import com.zebrunner.reporting.web.util.serializer.ToJsonDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestSessionDTO {

    @Positive
    private Long id;

    @NotEmpty(groups = ValidationGroups.onSessionStart.class)
    private String sessionId;

    @NotNull(groups = ValidationGroups.onSessionStart.class)
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;

    @JsonDeserialize(using = ToJsonDeserializer.class)
    @JsonSerialize(using = FromJsonSerializer.class)
    private String desiredCapabilities;

    @JsonDeserialize(using = ToJsonDeserializer.class)
    @JsonSerialize(using = FromJsonSerializer.class)
    private String capabilities;
    private Set<Long> testRefs = new HashSet<>();

    public static class ValidationGroups {
        public interface onSessionStart {}
        public interface onSessionEnd {} // needs to apply on dozer mapper
    }

}
