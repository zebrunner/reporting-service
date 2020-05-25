package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.TestResult;
import com.zebrunner.reporting.domain.dto.TestResultDTO;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestCaseSearchCriteria;
import com.zebrunner.reporting.domain.db.TestCase;
import com.zebrunner.reporting.domain.db.TestMetric;
import com.zebrunner.reporting.domain.dto.TestCaseType;
import com.zebrunner.reporting.service.TestCaseService;
import com.zebrunner.reporting.service.TestMetricService;
import com.zebrunner.reporting.service.TestService;
import com.zebrunner.reporting.web.documented.TestCaseDocumentedController;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping(path = "api/tests/cases", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class TestCaseController extends AbstractController implements TestCaseDocumentedController {

    private final Mapper mapper;
    private final TestCaseService testCaseService;
    private final TestService testService;
    private final TestMetricService testMetricService;

    @PostMapping("/search")
    @Override
    public SearchResult<TestCase> searchTestCases(@Valid @RequestBody TestCaseSearchCriteria sc) {
        return testCaseService.searchTestCases(sc);
    }

    @GetMapping("/{id}/metrics")
    @Override
    public Map<String, List<TestMetric>> getTestMetricsByTestCaseId(@PathVariable("id") Long id) {
        return testMetricService.getTestMetricsByTestCaseId(id);
    }

    @PostMapping()
    @Override
    public TestCaseType createTestCase(
            @RequestBody @Valid TestCaseType testCase,
            @RequestHeader(name = "Project", required = false) String projectName
    ) {
        TestCase tc = mapper.map(testCase, TestCase.class);
        return mapper.map(testCaseService.createOrUpdateCase(tc, projectName), TestCaseType.class);
    }

    @PostMapping("/batch")
    @Override
    public TestCaseType[] createTestCases(
            @RequestBody @Valid TestCaseType[] tcs,
            @RequestHeader(name = "Project", required = false) String projectName
    ) {
        if (ArrayUtils.isEmpty(tcs)) {
            return new TestCaseType[0];
        }
        TestCase[] testCases = Arrays.stream(tcs)
                                     .map(testCaseType -> mapper.map(testCaseType, TestCase.class))
                                     .toArray(TestCase[]::new);
        testCases = testCaseService.createOrUpdateCases(testCases, projectName);

        tcs = Arrays.stream(testCases)
                    .map(testCase -> mapper.map(testCase, TestCaseType.class))
                    .toArray(TestCaseType[]::new);
        return tcs;
    }

}