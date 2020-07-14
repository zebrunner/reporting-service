package com.zebrunner.reporting.web.request.v1;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PastOrPresent;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@JGlobalMap
@NoArgsConstructor
public class TestSessionFinishRequest {

    @PastOrPresent
    private OffsetDateTime endedAt;

    private Set<Long> testRefs = new HashSet<>();

}
