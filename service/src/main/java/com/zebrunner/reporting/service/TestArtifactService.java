package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.TestArtifact;
import com.zebrunner.reporting.persistence.dao.mysql.application.TestArtifactMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestArtifactService {

    private final TestArtifactMapper testArtifactMapper;

    @Autowired
    public TestArtifactService(TestArtifactMapper testArtifactMapper) {
        this.testArtifactMapper = testArtifactMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createTestArtifact(TestArtifact testArtifact) {
        testArtifactMapper.createTestArtifact(testArtifact);
    }

    @Transactional(rollbackFor = Exception.class)
    public TestArtifact updateTestArtifact(TestArtifact testArtifact) {
        testArtifactMapper.updateTestArtifact(testArtifact);
        return testArtifact;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTestArtifactsByTestId(Long testId) {
        testArtifactMapper.deleteTestArtifactsByTestId(testId);
    }

    @Transactional(readOnly = true)
    public TestArtifact getTestArtifactByNameAndTestId(String name, long testId) {
        return testArtifactMapper.getTestArtifactByNameAndTestId(name, testId);
    }

    @Transactional(rollbackFor = Exception.class)
    public TestArtifact createOrUpdateTestArtifact(TestArtifact newTestArtifact) {
        TestArtifact testArtifact = getTestArtifactByNameAndTestId(newTestArtifact.getName(), newTestArtifact.getTestId());
        if (testArtifact == null) {
            createTestArtifact(newTestArtifact);
        } else {
            newTestArtifact.setId(testArtifact.getId());
            updateTestArtifact(newTestArtifact);
        }
        return newTestArtifact;
    }
}
