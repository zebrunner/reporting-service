package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.TestRunArtifact;
import com.zebrunner.reporting.persistence.dao.mysql.application.TestRunArtifactMapper;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.TEST_RUN_ARTIFACT_CAN_NOT_BE_CREATED;

@Service
public class TestRunArtifactService {

    private static final String TEST_RUN_ARTIFACT_ALREADY_EXISTS = "TestRunArtifact with name '%s' already exists";
    private final TestRunArtifactMapper testRunArtifactMapper;

    @Autowired
    public TestRunArtifactService(TestRunArtifactMapper testRunArtifactMapper) {
        this.testRunArtifactMapper = testRunArtifactMapper;
    }

    @Transactional
    public TestRunArtifact createTestRunArtifact(TestRunArtifact testRunArtifact) {
        TestRunArtifact dbTestRunArtifact = getTestRunArtifactByNameAndTestRunId(testRunArtifact.getName(), testRunArtifact.getTestRunId());
        if (dbTestRunArtifact != null) {
            throw new IllegalOperationException(TEST_RUN_ARTIFACT_CAN_NOT_BE_CREATED, String.format(TEST_RUN_ARTIFACT_ALREADY_EXISTS, dbTestRunArtifact.getName()));
        }
        testRunArtifactMapper.createTestRunArtifact(testRunArtifact);
        return testRunArtifact;
    }

    @Transactional(readOnly = true)
    public TestRunArtifact getTestRunArtifactByNameAndTestRunId(String name, long testRunId) {
        return testRunArtifactMapper.getTestRunArtifactByNameAndTestRunId(name, testRunId);
    }

}
