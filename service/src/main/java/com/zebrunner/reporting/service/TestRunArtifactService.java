package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.TestRunArtifact;
import com.zebrunner.reporting.persistence.dao.mysql.application.TestRunArtifactMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestRunArtifactService {

    private final TestRunArtifactMapper testRunArtifactMapper;

    @Autowired
    public TestRunArtifactService(TestRunArtifactMapper testRunArtifactMapper) {
        this.testRunArtifactMapper = testRunArtifactMapper;
    }

    @Transactional
    public void createTestRunArtifact(TestRunArtifact testRunArtifact) {
        testRunArtifactMapper.createTestRunArtifact(testRunArtifact);
    }

    @Transactional(rollbackFor = Exception.class)
    public TestRunArtifact updateTestRunArtifact(TestRunArtifact testRunArtifact) {
        testRunArtifactMapper.updateTestRunArtifact(testRunArtifact);
        return testRunArtifact;
    }

    @Transactional
    public void deleteTestRunArtifactsByTestRunId(Long testRunId) {
        testRunArtifactMapper.deleteTestRunArtifactsByTestRunId(testRunId);
    }

    @Transactional(readOnly = true)
    public TestRunArtifact getTestRunArtifactByNameAndTestRunId(String name, long testRunId) {
        return testRunArtifactMapper.getTestRunArtifactByNameAndTestRunId(name, testRunId);
    }

    @Transactional
    public TestRunArtifact createOrUpdateTestRunArtifact(TestRunArtifact testRunArtifact) {
        TestRunArtifact dbTestRunArtifact = getTestRunArtifactByNameAndTestRunId(testRunArtifact.getName(), testRunArtifact.getTestRunId());
        if (dbTestRunArtifact == null) {
            createTestRunArtifact(testRunArtifact);
        } else {
            testRunArtifact.setId(dbTestRunArtifact.getId());
            updateTestRunArtifact(testRunArtifact);
        }
        return testRunArtifact;
    }
}
