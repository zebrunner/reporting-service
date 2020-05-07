package com.zebrunner.reporting.service.cache.impl;

import com.zebrunner.reporting.persistence.dao.mysql.application.TestRunMapper;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.dto.TestRunStatistics;
import com.zebrunner.reporting.service.cache.TestRunStatisticsCacheableService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service uses for test runs caching
 * <h1>(isolation need for Spring target proxy objects)</h1>
 */
@Service
public class TestRunStatisticsCacheableServiceImpl implements TestRunStatisticsCacheableService {

    private static final String TEST_RUN_STATISTICS_CACHE_NAME = "testRunStatistics";

    private final TestRunMapper testRunMapper;

    public TestRunStatisticsCacheableServiceImpl(TestRunMapper testRunMapper) {
        this.testRunMapper = testRunMapper;
    }

    /**
     * Get and put (unique) into cache test run statistic by {@link TestRun} id key
     * 
     * @param testRunId - to get statistic for
     * @return test run statistics
     */
    @Cacheable(value = TEST_RUN_STATISTICS_CACHE_NAME, unless = "#result == null", condition = "#testRunId != null", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #testRunId")
    @Transactional(readOnly = true)
    @Override
    public TestRunStatistics getTestRunStatistic(Long testRunId) {
        return testRunMapper.getTestRunStatistics(testRunId);
    }

    @CachePut(value = TEST_RUN_STATISTICS_CACHE_NAME, condition = "#statistic != null", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #statistic.testRunId")
    @Override
    public TestRunStatistics setTestRunStatistic(TestRunStatistics statistic) {
        return statistic;
    }

    /**
     * Evict all entries from cache in several hours after start crone expression
     * <h2>0 0 0/4 ? * * *</h2> - every 4 hours
     */
    @CacheEvict(value = TEST_RUN_STATISTICS_CACHE_NAME, allEntries = true)
    @Scheduled(cron = "0 0 0/4 ? * * *")
    @Override
    public void cacheEvict() {
    }
}
