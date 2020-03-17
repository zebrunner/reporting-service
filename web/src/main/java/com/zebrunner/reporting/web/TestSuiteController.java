package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.TestSuite;
import com.zebrunner.reporting.domain.dto.TestSuiteType;
import com.zebrunner.reporting.service.TestSuiteService;
import com.zebrunner.reporting.web.documented.TestSuiteDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping(path = "api/tests/suites", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestSuiteController extends AbstractController implements TestSuiteDocumentedController {

    private final Mapper mapper;
    private final TestSuiteService testSuiteService;

    public TestSuiteController(Mapper mapper, TestSuiteService testSuiteService) {
        this.mapper = mapper;
        this.testSuiteService = testSuiteService;
    }

    @PostMapping()
    @Override
    public TestSuiteType createTestSuite(@RequestBody @Valid TestSuiteType testSuite) {
        return mapper.map(testSuiteService.createOrUpdateTestSuite(mapper.map(testSuite, TestSuite.class)), TestSuiteType.class);
    }

}
