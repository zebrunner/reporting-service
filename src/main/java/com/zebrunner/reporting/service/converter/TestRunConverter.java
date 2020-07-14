package com.zebrunner.reporting.service.converter;

import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.db.Project;
import com.zebrunner.reporting.domain.db.TestSuite;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.reporting.TestRun;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TestRunConverter {

    public com.zebrunner.reporting.domain.db.TestRun toLegacyModel(TestRun testRun, String projectKey, Long userId) {
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
        oldTestRun.setStartedAt(new Date(testRun.getStartedAt().toInstant().toEpochMilli()));

        if (testRun.getLaunchContext() != null) {
            Job job = new Job();
            // conversion to long should be adjusted in future
            job.setId(Long.valueOf(testRun.getLaunchContext().getJobNumber()));
            oldTestRun.setJob(job);

            if (testRun.getLaunchContext().getUpstreamJobNumber() != null) {
                Job upstreamJob = new Job();
                // conversion to long should be adjusted in future
                upstreamJob.setId(Long.valueOf(testRun.getLaunchContext().getUpstreamJobNumber()));
                oldTestRun.setUpstreamJob(upstreamJob);
            }
        }

        return oldTestRun;
    }

    public TestSuite toTestSuite(TestRun testRun, Long userId) {
        User user = new User();
        user.setId(userId);

        TestSuite testSuite = new TestSuite();
        testSuite.setName(testRun.getName());
        testSuite.setFileName(testRun.getName());
        testSuite.setUser(user);

        return testSuite;
    }

}
