package com.zebrunner.reporting.web.v1;

import com.zebrunner.reporting.domain.db.reporting.TestSession;
import com.zebrunner.reporting.service.reporting.TestSessionServiceV1;
import com.zebrunner.reporting.web.dto.TestSessionDTO;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
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

    private final Mapper mapper;
    private final TestSessionServiceV1 testSessionService;

    @PostMapping
    public TestSessionDTO startSession(@RequestBody @Validated(TestSessionDTO.ValidationGroups.onSessionStart.class) TestSessionDTO testSessionDTO) {
        TestSession session = mapper.map(testSessionDTO, TestSession.class, TestSessionDTO.ValidationGroups.onSessionStart.class.getName());
        session = testSessionService.create(session);
        return mapper.map(session, TestSessionDTO.class, TestSessionDTO.ValidationGroups.onSessionStart.class.getName());
    }

    @PutMapping("/{id}")
    public TestSessionDTO updateSession(@RequestBody TestSessionDTO testSessionDTO,
                                        @PathVariable("id") @Positive Long id) {
        TestSession session = mapper.map(testSessionDTO, TestSession.class, TestSessionDTO.ValidationGroups.onSessionEnd.class.getName());
        session.setId(id);
        session = testSessionService.updateAndLinkToTests(session);
        return mapper.map(session, TestSessionDTO.class, TestSessionDTO.ValidationGroups.onSessionEnd.class.getName());
    }

}
