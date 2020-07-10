package com.zebrunner.reporting.web.request.v1;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.OffsetDateTime;

@Data
@JGlobalMap
@NoArgsConstructor
public class TestRunFinishRequest {

    @NotNull
    @PastOrPresent
    private OffsetDateTime endedAt;

}
