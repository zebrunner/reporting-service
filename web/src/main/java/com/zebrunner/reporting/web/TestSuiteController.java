package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.TestRunResult;
import com.zebrunner.reporting.domain.db.TestSuite;
import com.zebrunner.reporting.domain.dto.TestRunResultDTO;
import com.zebrunner.reporting.domain.dto.TestSuiteType;
import com.zebrunner.reporting.service.TestRunService;
import com.zebrunner.reporting.service.TestSuiteService;
import com.zebrunner.reporting.web.documented.TestSuiteDocumentedController;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping(path = "api/tests/suites", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class TestSuiteController extends AbstractController implements TestSuiteDocumentedController {

    private final Mapper mapper;
    private final TestSuiteService testSuiteService;
    private final TestRunService testRunService;

    @PostMapping
    @Override
    public TestSuiteType createTestSuite(@RequestBody @Valid TestSuiteType testSuite) {
        return mapper.map(testSuiteService.createOrUpdateTestSuite(mapper.map(testSuite, TestSuite.class)), TestSuiteType.class);
    }

    @GetMapping("/{id}/results")
    @Override
    public List<TestRunResultDTO> getTestSuiteResultsById(@PathVariable("id") Long id,
                                                          @RequestParam("limit") Long limit) {
        List<TestRunResult> testCaseStability = testRunService.getTestRunResultsByTestSuiteId(id, limit);
        return testCaseStability.stream()
                                .map(stability -> mapper.map(stability, TestRunResultDTO.class))
                                .collect(Collectors.toList());
    }

}
