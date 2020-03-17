package com.zebrunner.reporting.service.cache.impl;

import com.zebrunner.reporting.persistence.dao.mysql.application.TestCaseMapper;
import com.zebrunner.reporting.domain.db.TestCase;
import com.zebrunner.reporting.service.cache.TestCaseCacheableService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestCaseCacheableServiceImpl implements TestCaseCacheableService {

    private static final String TEST_CASE_CACHE_NAME = "testCases";

    private final TestCaseMapper testCaseMapper;

    public TestCaseCacheableServiceImpl(TestCaseMapper testCaseMapper) {
        this.testCaseMapper = testCaseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = TEST_CASE_CACHE_NAME, unless="#result == null", key = "{ new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #userId, new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #testClass,  new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #testMethod, new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #projectId }")
    public TestCase getOwnedTestCase(Long userId, String testClass, String testMethod, Long projectId) {
        return testCaseMapper.getOwnedTestCase(userId, testClass, testMethod, projectId);
    }
}
