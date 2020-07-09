package com.zebrunner.reporting.service.cache;

import com.zebrunner.reporting.domain.dto.TestRunStatistics;

public interface TestRunStatisticsCacheableService {

    TestRunStatistics getTestRunStatistic(Long testRunId);

    TestRunStatistics setTestRunStatistic(TestRunStatistics statistic);

    void cacheEvict();
}
