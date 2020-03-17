package com.zebrunner.reporting.web;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestSessionSearchCriteria;
import com.zebrunner.reporting.domain.dto.testsession.SearchParameter;
import com.zebrunner.reporting.domain.dto.testsession.TokenDTO;
import com.zebrunner.reporting.domain.entity.TestSession;
import com.zebrunner.reporting.service.TestSessionService;
import com.zebrunner.reporting.web.documented.TestSessionDocumentedController;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "api/tests/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestSessionController extends AbstractController implements TestSessionDocumentedController {

    private final TestSessionService testSessionService;

    public TestSessionController(TestSessionService testSessionService) {
        this.testSessionService = testSessionService;
    }

    @GetMapping("/{sessionId}")
    @Override
    public TestSession getBySessionId(@PathVariable("sessionId") String sessionId) {
        return testSessionService.retrieveBySessionId(sessionId);
    }

    @GetMapping("/search")
    @Override
    public SearchResult<TestSession> search(TestSessionSearchCriteria criteria) {
        return testSessionService.search(criteria);
    }

    @GetMapping("/search/parameters")
    @Override
    public SearchParameter getSearchParameters() {
        return testSessionService.collectSearchParameters();
    }

    @PreAuthorize("hasPermission('REFRESH_TOKEN')")
    @GetMapping("/token/refresh")
    @Override
    public TokenDTO refreshToken(@RequestParam("integrationId") Long integrationId) {
        String token = testSessionService.refreshToken(integrationId);
        return new TokenDTO(token);
    }

}
