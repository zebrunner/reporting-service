package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.web.dto.TestDTO;
import com.zebrunner.reporting.web.dto.TestRunDTO;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@CrossOrigin
@RequestMapping(path = "api/v1/reporting", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ReportingController {

    private final Mapper mapper;

    public ReportingController(Mapper mapper) {
        this.mapper = mapper;
    }

    @PostMapping("/test-runs")
    public TestRunDTO startTestRun(
            @RequestBody @Validated(TestRunDTO.ValidationGroups.TestRunStartGroup.class) TestRunDTO testRunDTO,
            @RequestParam(name = "projectKey", required = false) String projectKey
    ) {
        TestRun testRun = mapper.map(testRunDTO, TestRun.class, TestRunDTO.ValidationGroups.TestRunStartGroup.class.getName());
        return testRunDTO;
    }

    @PutMapping("/test-runs/{id}")
    public void finishTestRun(
            @RequestBody @Validated(TestRunDTO.ValidationGroups.TestRunFinishGroup.class) TestRunDTO testRunDTO,
            @PathVariable("id") @NotNull @Positive Long id
    ) {
        TestRun testRun = mapper.map(testRunDTO, TestRun.class, TestRunDTO.ValidationGroups.TestRunFinishGroup.class.getName());
        testRun.setId(id);
    }

    @PostMapping("/test-runs/{id}/tests")
    public TestDTO startTest(
            @RequestBody @Validated(TestDTO.ValidationGroups.TestStartGroup.class) TestDTO testDTO,
            @PathVariable("id") @NotNull @Positive Long id
    ) {
        Test test = mapper.map(testDTO, Test.class, TestDTO.ValidationGroups.TestStartGroup.class.getName());
        return testDTO;
    }

    @PutMapping("/test-runs/{id}/tests/{testId}")
    public void finishTest(
            @RequestBody @Validated(TestDTO.ValidationGroups.TestFinishGroup.class) TestDTO testDTO,
            @PathVariable("id") @NotNull @Positive Long id,
            @PathVariable("testId") @NotNull @Positive Long testId
    ) {
        Test test = mapper.map(testDTO, Test.class, TestDTO.ValidationGroups.TestFinishGroup.class.getName());
        test.setId(testId);
    }
}
