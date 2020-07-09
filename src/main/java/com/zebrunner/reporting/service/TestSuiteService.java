package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.TestSuiteMapper;
import com.zebrunner.reporting.domain.db.TestSuite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestSuiteService {

    private final TestSuiteMapper testSuiteMapper;

    public TestSuiteService(TestSuiteMapper testSuiteMapper) {
        this.testSuiteMapper = testSuiteMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createTestSuite(TestSuite testSuite) {
        testSuiteMapper.createTestSuite(testSuite);
    }

    @Transactional(readOnly = true)
    public TestSuite getTestSuiteByIdFull(long id) {
        return testSuiteMapper.getTestSuiteByIdFull(id);
    }

    @Transactional(readOnly = true)
    public TestSuite getTestSuiteByNameAndFileNameAndUserId(String name, String fileName, long userId) {
        return testSuiteMapper.getTestSuiteByNameAndFileNameAndUserId(name, fileName, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public TestSuite updateTestSuite(TestSuite testSuite) {
        testSuiteMapper.updateTestSuite(testSuite);
        return testSuite;
    }

    @Transactional(rollbackFor = Exception.class)
    public TestSuite createOrUpdateTestSuite(TestSuite newTestSuite) {
        TestSuite testSuite = getTestSuiteByNameAndFileNameAndUserId(newTestSuite.getName(), newTestSuite.getFileName(),
                newTestSuite.getUser().getId());
        if (testSuite == null) {
            createTestSuite(newTestSuite);
        } else if (!testSuite.equals(newTestSuite)) {
            newTestSuite.setId(testSuite.getId());
            updateTestSuite(newTestSuite);
        } else {
            newTestSuite = testSuite;
        }
        return newTestSuite;
    }
}
