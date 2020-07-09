package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.TestSuite;
import org.apache.ibatis.annotations.Param;

public interface TestSuiteMapper {
    void createTestSuite(TestSuite testSuite);

    TestSuite getTestSuiteById(long id);

    TestSuite getTestSuiteByIdFull(long id);

    TestSuite getTestSuiteByName(String name);

    TestSuite getTestSuiteByNameAndFileNameAndUserId(@Param("name") String name, @Param("fileName") String fileName, @Param("userId") long userId);

    void updateTestSuite(TestSuite testSuite);

    void deleteTestSuiteById(long id);

    void deleteTestSuite(TestSuite testSuite);
}
