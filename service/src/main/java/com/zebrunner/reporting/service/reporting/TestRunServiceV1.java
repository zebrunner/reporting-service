package com.zebrunner.reporting.service.reporting;

import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.db.Project;
import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.Tag;
import com.zebrunner.reporting.domain.db.TestCase;
import com.zebrunner.reporting.domain.db.TestSuite;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.reporting.Test;
import com.zebrunner.reporting.domain.db.reporting.TestRun;
import com.zebrunner.reporting.persistence.dao.mysql.application.TestMapper;
import com.zebrunner.reporting.service.TestCaseService;
import com.zebrunner.reporting.service.TestRunService;
import com.zebrunner.reporting.service.TestService;
import com.zebrunner.reporting.service.TestSuiteService;
import com.zebrunner.reporting.service.UserService;
import com.zebrunner.reporting.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestRunServiceV1 {

    private final TestMapper testMapper;
    private final TestService testService;
    private final UserService userService;
    private final ProjectService projectService;
    private final TestRunService testRunService;
    private final TestCaseService testCaseService;
    private final TestSuiteService testSuiteService;

    @Transactional
    public TestRun startRun(TestRun testRun, String projectKey, Long userId) {
        Project project = projectService.getProjectByNameOrDefault(projectKey);

        TestSuite testSuite = convertToSuite(testRun, userId);
        testSuite = testSuiteService.createOrUpdateTestSuite(testSuite);

        com.zebrunner.reporting.domain.db.TestRun oldTestRun = convertToOldTestRun(testRun, project.getName(), userId);
        oldTestRun.setTestSuite(testSuite);
        oldTestRun = testRunService.startTestRun(oldTestRun);

        testRun.setId(oldTestRun.getId());
        return testRun;
    }

    @Transactional
    public void finishRun(TestRun testRun) {
        com.zebrunner.reporting.domain.db.TestRun oldTestRun = testRunService.getNotNullTestRunById(testRun.getId());
        oldTestRun.setEndedAt(new Date(testRun.getEndedAt().toInstant().toEpochMilli()));
        testRunService.calculateTestRunResult(oldTestRun, true);
    }

    @Transactional
    public Test startTest(Test test, Long testRunId, boolean headless, boolean rerun) {
        if (headless) {
            setDefaultHeadlessTestValues(test);
        }
        TestCase testCase = convertToTestCase(test, testRunId);
        testCase = testCaseService.createOrUpdateCase(testCase, testCase.getProject().getName());

        com.zebrunner.reporting.domain.db.Test oldTest = convertToOldTest(test, testRunId);
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
//        }

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
    public com.zebrunner.reporting.domain.db.Test updateTest(Test test, Long testRunId, boolean headless) {
        com.zebrunner.reporting.domain.db.Test oldTest = convertToOldTest(test, testRunId);
        if (headless) {
            TestCase testCase = convertToTestCase(test, testRunId);
            testCase = testCaseService.createOrUpdateCase(testCase, testCase.getProject().getName());

            com.zebrunner.reporting.domain.db.Test existingTest = testService.getTestById(test.getId());
            existingTest.setUuid(oldTest.getUuid());
            existingTest.setName(oldTest.getName());
            existingTest.setTestClass(oldTest.getTestClass());
            existingTest.setOwner(oldTest.getOwner());
            existingTest.setTestCaseId(testCase.getId());
            existingTest.setTags(oldTest.getTags());
            return testService.updateTest(existingTest);
        } else {
            Status status = Status.valueOf(test.getResult());
            oldTest.setStatus(status);
            oldTest.setMessage(test.getReason());
            oldTest.setFinishTime(new Date(test.getEndedAt().toInstant().toEpochMilli()));
            return testService.finishTest(oldTest, null, null);
        }
    }

    @Transactional(readOnly = true)
    public List<Test> getTestsByCiRunId(String ciRunId, List<Status> statuses, List<Long> testIds) {
        List<com.zebrunner.reporting.domain.db.Test> oldTests = testService.getTestsByTestRunId(ciRunId);
        return oldTests.stream()
                       .filter(oldTest -> statuses == null || statuses.contains(oldTest.getStatus()))
                       .filter(oldTest -> testIds == null || testIds.contains(oldTest.getId()))
                       .map(this::convertToTest)
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

    private TestSuite convertToSuite(TestRun testRun, Long userId) {
        User user = new User();
        user.setId(userId);

        TestSuite testSuite = new TestSuite();
        testSuite.setName(testRun.getName());
        testSuite.setFileName(testRun.getName());
        testSuite.setUser(user);

        return testSuite;
    }

    private com.zebrunner.reporting.domain.db.TestRun convertToOldTestRun(TestRun testRun, String projectKey, Long userId) {
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setName(projectKey);

        com.zebrunner.reporting.domain.db.TestRun oldTestRun = new com.zebrunner.reporting.domain.db.TestRun();
        oldTestRun.setId(testRun.getId());
        oldTestRun.setStartedBy(com.zebrunner.reporting.domain.db.TestRun.Initiator.HUMAN);
        oldTestRun.setBuildNumber(1);
        oldTestRun.setProject(project);
        oldTestRun.setFramework(testRun.getFramework());
        oldTestRun.setConfig(testRun.getConfig());
        oldTestRun.setUser(user);
        oldTestRun.setCiRunId(testRun.getUuid());

        if (testRun.getLaunchContext() != null) {
            Job job = new Job();
            job.setId(Long.valueOf(testRun.getLaunchContext().getJobNumber()));
            oldTestRun.setJob(job);

            Job upstreamJob = new Job();
            upstreamJob.setId(Long.valueOf(testRun.getLaunchContext().getUpstreamJobNumber()));
            oldTestRun.setUpstreamJob(upstreamJob);
        }

        return oldTestRun;
    }

    private TestCase convertToTestCase(Test test, Long testRunId) {
        User caseOwner = userService.getUserByUsername(test.getMaintainer());
        if (caseOwner == null) {
            caseOwner = userService.getUserByUsername("anonymous");
        }

        com.zebrunner.reporting.domain.db.TestRun testRun = testRunService.getNotNullTestRunById(testRunId);

        TestCase testCase = new TestCase();
        testCase.setTestClass(test.getClassName());
        testCase.setTestMethod(test.getMethodName());
        testCase.setInfo(test.getTestCase()); // for now in info
        testCase.setPrimaryOwner(caseOwner);
        testCase.setProject(testRun.getProject());
        testCase.setTestSuiteId(testRun.getTestSuite().getId());

        return testCase;
    }

    private com.zebrunner.reporting.domain.db.Test convertToOldTest(Test test, Long runId) {
        com.zebrunner.reporting.domain.db.Test oldTest = new com.zebrunner.reporting.domain.db.Test();
        oldTest.setId(test.getId() == null ? 0 : test.getId());
        oldTest.setName(test.getName());
        oldTest.setTestRunId(runId);
        oldTest.setOwner(test.getMaintainer());
        oldTest.setUuid(test.getUuid());

        if (test.getTags() != null) {
            Set<Tag> tags = test.getTags().stream()
                                .map(tag -> new Tag(tag, null))
                                .collect(Collectors.toSet());
            oldTest.setTags(tags);
        }

        // TODO: 3/20/20 additional attributes

        return oldTest;
    }

    private Test convertToTest(com.zebrunner.reporting.domain.db.Test oldTest) {
        TestCase testCase = testCaseService.getTestCaseById(oldTest.getTestCaseId());

        Test test = new Test();
        test.setId(oldTest.getId());
        test.setUuid(oldTest.getUuid());
        test.setName(oldTest.getName());
        test.setMaintainer(oldTest.getOwner());
        test.setResult(oldTest.getStatus().name());

        test.setClassName(testCase.getTestClass());
        test.setMethodName(testCase.getTestMethod());
        test.setTestCase(testCase.getInfo());

        return test;
    }
}
