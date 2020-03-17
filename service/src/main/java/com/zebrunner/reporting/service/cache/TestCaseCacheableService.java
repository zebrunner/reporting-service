package com.zebrunner.reporting.service.cache;

import com.zebrunner.reporting.domain.db.TestCase;

public interface TestCaseCacheableService {

    TestCase getOwnedTestCase(Long userId, String testClass, String testMethod, Long projectId);

}
