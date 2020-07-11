package com.zebrunner.reporting.service.reporting;

import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.TestCase;
import com.zebrunner.reporting.domain.db.TestSuite;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.reporting.Test;
import com.zebrunner.reporting.domain.db.reporting.TestRun;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestCaseSearchCriteria;
import com.zebrunner.reporting.service.TestCaseService;
import com.zebrunner.reporting.service.TestRunService;
import com.zebrunner.reporting.service.TestService;
import com.zebrunner.reporting.service.TestSuiteService;
import com.zebrunner.reporting.service.UserService;
import com.zebrunner.reporting.service.converter.TestConverter;
import com.zebrunner.reporting.service.converter.TestRunConverter;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail;
import com.zebrunner.reporting.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestRunServiceV1 {

    private final TestService testService;
    private final UserService userService;
    private final TestConverter testConverter;
    private final ProjectService projectService;
    private final TestRunService testRunService;
    private final TestCaseService testCaseService;
    private final TestSuiteService testSuiteService;
    private final TestRunConverter testRunConverter;

    @Transactional
    public TestRun startRun(TestRun testRun, String projectKey, Long userId) {
        if (projectKey == null) {
            projectKey = ProjectService.DEFAULT_PROJECT;
        } else if (projectService.getProjectByName(projectKey) == null) {
            throw new ResourceNotFoundException(ResourceNotFoundErrorDetail.PROJECT_NOT_FOUND, "Project with name " + projectKey + " does not exists");
        }

        TestSuite testSuite = testRunConverter.toTestSuite(testRun, userId);
        testSuite = testSuiteService.createOrUpdateTestSuite(testSuite);

        com.zebrunner.reporting.domain.db.TestRun legacyTestRun = testRunConverter.toLegacyModel(
                testRun, projectKey, userId
        );
        legacyTestRun.setTestSuite(testSuite);
        legacyTestRun = testRunService.startTestRun(legacyTestRun);

        testRun.setId(legacyTestRun.getId());
        testRun.setUuid(legacyTestRun.getCiRunId());
        return testRun;
    }

    @Transactional
    public void finishRun(TestRun testRun) {
        com.zebrunner.reporting.domain.db.TestRun oldTestRun = testRunService.getNotNullTestRunById(testRun.getId());
        oldTestRun.setEndedAt(new Date(testRun.getEndedAt().toInstant().toEpochMilli()));
        testRunService.calculateTestRunResult(oldTestRun, true);
    }

    @Transactional
    public Test startTest(Test test, Long testRunId, boolean rerun) {
        setDefaultHeadlessTestValues(test);
        TestCase testCase = buildTestCase(test, testRunId);
        testCase = testCaseService.createOrUpdateCase(testCase, testCase.getProject().getName());

        com.zebrunner.reporting.domain.db.Test oldTest = testConverter.toLegacyModel(test, testRunId);
        oldTest.setTestCaseId(testCase.getId());
        oldTest.setStartTime(new Date(test.getStartedAt().toInstant().toEpochMilli()));

//        com.zebrunner.reporting.domain.db.Test headlessTest = null;
//        if (test.getId() != null) { // headless test override (next headless test or test start)
//            headlessTest = testService.getTestById(test.getId());
//            if (headlessTest != null) {
//                oldTest.setUuid(test.getUuid());
//                oldTest.setStartTime(headlessTest.getStartTime());
//            }
//        }

//        boolean updateHeadlessTest = false;
//        com.zebrunner.reporting.domain.db.Test existingTest = testMapper.getTestByTestRunIdAndUuid(testRunId, test.getUuid());
//        if (existingTest != null) {
//            oldTest.setId(existingTest.getId());
//            if (headless && !rerun) { // if there are many headless tests in chain
//                oldTest.setStatus(existingTest.getStatus());
//                updateHeadlessTest = true;
//            }
//        }
//
//        if (updateHeadlessTest) {
//            oldTest = testService.updateTest(oldTest);
//        } else {
        oldTest = testService.startTest(oldTest, null, null, rerun);

        test.setId(oldTest.getId());
        return test;
    }

    private void setDefaultHeadlessTestValues(Test test) {
        if (test.getName() == null) {
            test.setName("system");
        }
        if (test.getClassName() == null) {
            test.setClassName("system");
        }
        if (test.getMethodName() == null) {
            test.setMethodName("system");
        }
    }

    @Transactional
    public void updateTest(Test test, Long testRunId) {
        com.zebrunner.reporting.domain.db.Test oldTest = testConverter.toLegacyModel(test, testRunId);

        TestCase testCase = buildTestCase(test, testRunId);
        testCase = testCaseService.createOrUpdateCase(testCase, testCase.getProject().getName());

        com.zebrunner.reporting.domain.db.Test existingTest = testService.getTestById(test.getId());
        existingTest.setUuid(oldTest.getUuid());
        existingTest.setName(oldTest.getName());
        existingTest.setOwner(oldTest.getOwner());
        existingTest.setTestCaseId(testCase.getId());
        existingTest.setTags(oldTest.getTags());

        testService.updateTest(existingTest);
    }

    @Transactional
    public void finishTest(Test test, Long testRunId) {
        com.zebrunner.reporting.domain.db.Test oldTest = testConverter.toLegacyModel(test, testRunId);

        Status status = Status.valueOf(test.getResult());
        oldTest.setStatus(status);
        oldTest.setMessage(test.getReason());
        oldTest.setFinishTime(new Date(test.getEndedAt().toInstant().toEpochMilli()));

        testService.finishTest(oldTest, null, null);
    }

    @Transactional(readOnly = true)
    public List<Test> getTestsByCiRunId(String ciRunId, List<Status> statuses, List<Long> testIds) {
        List<com.zebrunner.reporting.domain.db.Test> oldTests = testService.getTestsByTestRunId(ciRunId);
        List<Long> testCaseIds = oldTests.stream()
                                         .map(com.zebrunner.reporting.domain.db.Test::getTestCaseId)
                                         .collect(Collectors.toList());

        Map<Long, TestCase> idToCase = testCaseService.searchTestCases(new TestCaseSearchCriteria(testCaseIds))
                                                      .getResults().stream()
                                                      .collect(Collectors.toMap(TestCase::getId, Function.identity()));

        return oldTests.stream()
                       .filter(oldTest -> statuses == null || statuses.contains(oldTest.getStatus()))
                       .filter(oldTest -> testIds == null || testIds.contains(oldTest.getId()))
                       .map(test -> testConverter.fromLegacyModel(test, idToCase.get(test.getTestCaseId())))
                       .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public com.zebrunner.reporting.domain.db.TestRun getTestRunFullById(Long id) {
        return testRunService.getTestRunByIdFull(id);
    }

    @Transactional(readOnly = true)
    public com.zebrunner.reporting.domain.db.Test getTestById(Long id) {
        return testService.getTestById(id);
    }

    private TestCase buildTestCase(Test test, Long testRunId) {
        User caseOwner = userService.getByUsername(test.getMaintainer());
        if (caseOwner == null) {
            caseOwner = userService.getByUsername("anonymous");
        }

        com.zebrunner.reporting.domain.db.TestRun testRun = testRunService.getNotNullTestRunById(testRunId);
        return testConverter.toTestCase(test, testRun, caseOwner);
    }

}
