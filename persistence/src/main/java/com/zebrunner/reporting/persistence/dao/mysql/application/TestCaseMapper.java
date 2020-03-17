package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.List;

import com.zebrunner.reporting.domain.db.TestCase;
import org.apache.ibatis.annotations.Param;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestCaseSearchCriteria;

public interface TestCaseMapper {
    void createTestCase(TestCase testCase);

    TestCase getTestCaseById(long id);

    List<TestCase> getTestCasesByUsername(String username);

    TestCase getOwnedTestCase(@Param("userId") Long userId, @Param("testClass") String testClass, @Param("testMethod") String testMethod, @Param("projectId") Long projectId);

    void updateTestCase(TestCase testCase);

    void reassignToProject(@Param("fromProjectId") Long fromProjectId, @Param("toProjectId") Long toProjectId);

    void deleteTestCaseById(long id);

    void deleteTestCase(TestCase testCase);

    List<TestCase> searchTestCases(TestCaseSearchCriteria sc);

    Integer getTestCasesSearchCount(TestCaseSearchCriteria sc);
}
