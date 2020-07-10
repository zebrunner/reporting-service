package com.zebrunner.reporting.web.v1;

import com.zebrunner.reporting.domain.db.reporting.TestSession;
import com.zebrunner.reporting.service.reporting.TestSessionServiceV1;
import com.zebrunner.reporting.web.request.v1.TestSessionFinishRequest;
import com.zebrunner.reporting.web.request.v1.TestSessionStartRequest;
import com.zebrunner.reporting.web.response.v1.TestSessionSaveResponse;
import com.zebrunner.reporting.web.util.JMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@Validated
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "v1/test-sessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestSessionControllerV1 {

    private final JMapper jMapper;
    private final TestSessionServiceV1 testSessionService;

    @PostMapping
    public TestSessionSaveResponse startSession(@RequestBody @Validated TestSessionStartRequest testSessionStartRequest) {
        TestSession session = jMapper.map(testSessionStartRequest, TestSession.class);
        session = testSessionService.create(session);
        return jMapper.map(session, TestSessionSaveResponse.class);
    }

    @PutMapping("/{id}")
    public TestSessionSaveResponse updateSession(@RequestBody @Validated TestSessionFinishRequest testSessionFinishRequest,
                                                 @PathVariable("id") @Positive Long id) {
        TestSession session = jMapper.map(testSessionFinishRequest, TestSession.class);
        session.setId(id);
        session = testSessionService.updateAndLinkToTests(session);
        return jMapper.map(session, TestSessionSaveResponse.class);
    }

}
