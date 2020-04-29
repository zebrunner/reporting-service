package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.TestRunArtifact;
import com.zebrunner.reporting.persistence.dao.mysql.application.TestRunArtifactMapper;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.TEST_RUN_ARTIFACT_CAN_NOT_BE_CREATED;

@Service
public class TestRunArtifactService {

    private static final String TEST_RUN_ARTIFACT_ALREADY_EXISTS = "Test run artifacts with names '%s' already exist";
    private final TestRunArtifactMapper testRunArtifactMapper;

    @Autowired
    public TestRunArtifactService(TestRunArtifactMapper testRunArtifactMapper) {
        this.testRunArtifactMapper = testRunArtifactMapper;
    }

    @Transactional
    public List<TestRunArtifact> createTestRunArtifacts(List<TestRunArtifact> testRunArtifacts) {
        List<String> alreadyExistingNames = testRunArtifacts.stream()
                                                            .filter(artifact -> existsByNameAndTestRunId(artifact.getName(), artifact.getTestRunId()))
                                                            .map(TestRunArtifact::getName)
                                                            .collect(Collectors.toList());
        if (!alreadyExistingNames.isEmpty()) {
            String names = String.join(", ", alreadyExistingNames);
            throw new IllegalOperationException(TEST_RUN_ARTIFACT_CAN_NOT_BE_CREATED, String.format(TEST_RUN_ARTIFACT_ALREADY_EXISTS, names));
        }
        testRunArtifactMapper.createTestRunArtifacts(testRunArtifacts);
        return testRunArtifacts;
    }

    @Transactional(readOnly = true)
    public boolean existsByNameAndTestRunId(String name, Long testRunId) {
        return testRunArtifactMapper.existsByNameAndTestRunId(name, testRunId);
    }
}
