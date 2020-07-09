package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.Tag;
import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.TestResult;
import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import org.apache.ibatis.annotations.Param;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestSearchCriteria;

public interface TestMapper {
    void createTest(Test test);

    void addTags(@Param(value = "testId") Long testId, @Param(value = "tags") Set<Tag> tags);

    Test getTestById(long id);

    Test getTestByTestRunIdAndUuid(@Param("testRunId") Long testRunId, @Param("uuid") String uuid);

    Test getTestByIdAndTestRunId(@Param("id") long id, @Param("testRunId") long testRunId);

    boolean existsTestByIdAndTestRunId(@Param("id") long id, @Param("testRunId") long testRunId);

    List<Test> getTestsByTestRunId(long testRunId);

    List<TestResult> getTestResultsByStartTimeAndTestCaseId(@Param("testCaseId") Long testCaseId,
                                                            @Param("startTime") Date startTime,
                                                            @Param("limit") Long limit);

    List<Test> getTestsByTestRunCiRunId(String ciRunId);

    List<Test> getTestsByTestRunIdAndStatus(@Param("testRunId") long testRunId, @Param("status") Status status);

    List<Test> getTestsByWorkItemId(long workItemId);

    void createTestWorkItem(@Param("test") Test test, @Param("workItem") WorkItem workItem);

    void deleteTestWorkItemByWorkItemIdAndTestId(@Param("workItemId") long workItemId, @Param("testId") long testId);

    void deleteTestWorkItemByTestIdAndWorkItemType(@Param("testId") long testId, @Param("type") WorkItem.Type type);

    void updateTest(Test test);

    void updateStatuses(@Param("ids") List<Long> ids, @Param("status") Status status);

    void updateTestsNeedRerun(@Param("ids") List<Long> ids, @Param("rerun") boolean needRerun);

    void deleteTestById(long id);

    void deleteTestByTestRunIdAndNameAndStatus(@Param("testRunId") long testRunId, @Param("testName") String testName,
                                               @Param("status") Status status);

    void deleteTest(Test test);

    void deleteTag(@Param(value = "testId") Long testId, @Param(value = "tagId") Long tagId);

    void deleteTags(@Param(value = "testId") Long testId);

    List<Test> searchTests(TestSearchCriteria sc);

    Integer getTestsSearchCount(TestSearchCriteria sc);
}
