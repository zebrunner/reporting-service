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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    private final TestRunService testRunService;
    private final TestSuiteService testSuiteService;
    private final TestService testService;
    private final TestMapper testMapper;
    private final TestCaseService testCaseService;
    private final UserService userService;
    private final ProjectService projectService;

    public ReportingService(TestRunService testRunService, TestSuiteService testSuiteService, TestService testService, TestMapper testMapper, TestCaseService testCaseService, UserService userService, ProjectService projectService) {
        this.testRunService = testRunService;
        this.testSuiteService = testSuiteService;
        this.testService = testService;
        this.testMapper = testMapper;
        this.testCaseService = testCaseService;
        this.userService = userService;
        this.projectService = projectService;
    }

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
    public TestRun finishRun(TestRun testRun) {
        com.zebrunner.reporting.domain.db.TestRun oldTestRun = testRunService.getNotNullTestRunById(testRun.getId());
        oldTestRun.setEndedAt(Timestamp.valueOf(testRun.getEndedAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        oldTestRun = testRunService.calculateTestRunResult(oldTestRun, true);
        return testRun;
    }

    @Transactional
    public Test startTest(Test test, Long testRunId) {
        TestCase testCase = convertToTestCase(test, testRunId);
        testCase = testCaseService.createOrUpdateCase(testCase, testCase.getProject().getName());

        com.zebrunner.reporting.domain.db.Test oldTest = convertToOldTest(test, testRunId);
        oldTest.setTestCaseId(testCase.getId());
        oldTest.setStartTime(Timestamp.valueOf(test.getStartedAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));

        com.zebrunner.reporting.domain.db.Test existingTest = testMapper.getTestByTestRunIdAndUuid(testRunId, test.getUuid());
        if (existingTest != null) {
            oldTest.setId(existingTest.getId());
        }

        oldTest = testService.startTest(oldTest, null, null);

        test.setId(oldTest.getId());
        return test;
    }

    @Transactional
    public Test finishTest(Test test, Long runId) {
        Status status = Status.valueOf(test.getResult());

        com.zebrunner.reporting.domain.db.Test oldTest = convertToOldTest(test, runId);
        oldTest.setStatus(status);
        oldTest.setMessage(test.getReason());
        oldTest.setFinishTime(Timestamp.valueOf(test.getEndedAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));

        testService.finishTest(oldTest, null, null);
        return test;
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
        User caseOwner = userService.getByUsername(test.getMaintainer());
        if (caseOwner == null) {
            caseOwner = userService.getByUsername("anonymous");
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
