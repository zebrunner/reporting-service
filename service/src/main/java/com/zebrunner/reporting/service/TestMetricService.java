package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.TestMetricMapper;
import com.zebrunner.reporting.domain.db.TestMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestMetricService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMetricService.class);

    @Autowired
    private TestMetricMapper testMetricMapper;

    @Transactional(readOnly = true)
    public List<String> getEnvsByTestCaseId(Long testCaseId) {
        return testMetricMapper.getEnvsByTestCaseId(testCaseId);
    }

    @Transactional(readOnly = true)
    public Map<String, List<TestMetric>> getTestMetricsByTestCaseId(Long testCaseId) {
        Map<String, List<TestMetric>> result = new HashMap<>();
        getEnvsByTestCaseId(testCaseId).forEach(env -> {
            List<TestMetric> testMetrics = testMetricMapper.getTestMetricsByTestCaseIdAndEnv(testCaseId, env);
            result.put(env, testMetrics);
        });
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createTestMetrics(Long testId, Map<String, Long> metrics) {
        try {
            if (metrics != null) {
                metrics.entrySet().stream()
                       .map(entry -> new TestMetric(entry.getKey(), entry.getValue(), testId))
                       .forEach(metric -> testMetricMapper.createTestMetric(metric));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to register test metrics: " + e.getMessage());
        }
    }
}