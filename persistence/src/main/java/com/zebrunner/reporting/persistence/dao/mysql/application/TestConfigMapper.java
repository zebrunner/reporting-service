package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.TestConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestConfigMapper {

    void createTestConfig(TestConfig testConfig);

    TestConfig getTestConfigById(long id);

    TestConfig searchTestConfig(@Param("testConfig") TestConfig testConfig);

    void updateTestConfig(TestConfig testConfig);

    void deleteTestConfigById(long id);

    List<String> getPlatforms();

    List<String> getBrowsers();

    List<String> getEnvironments();

    List<String> getLanguages();

    List<String> getLocales();

}
