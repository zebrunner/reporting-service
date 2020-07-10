package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.TestMetric;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestMetricMapper {
    void createTestMetric(TestMetric user);

    TestMetric getTestMetricById(long id);

    List<TestMetric> getTestMetricsByTestCaseIdAndEnv(@Param(value = "testCaseId") Long testCaseId, @Param(value = "env") String env);

    List<String> getEnvsByTestCaseId(Long testCaseId);

    void deleteTestMetricById(long id);
}
