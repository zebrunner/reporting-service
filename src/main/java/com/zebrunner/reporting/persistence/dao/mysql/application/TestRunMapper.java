package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.db.TestRunResult;
import com.zebrunner.reporting.domain.db.config.Argument;
import com.zebrunner.reporting.domain.dto.TestRunStatistics;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.JobSearchCriteria;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestRunSearchCriteria;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TestRunMapper {
    void createTestRun(TestRun testRun);

    TestRun getTestRunById(long id);

    TestRun getTestRunByIdFull(long id);

    TestRun getTestRunByCiRunId(String ciRunId);

    TestRun getTestRunByCiRunIdFull(String ciRunId);

    TestRun getLatestJobTestRunByBranch(@Param("branch") String branch, @Param("jobId") Long jobId);

    List<TestRunResult> getTestRunResultsByTestSuiteId(@Param("testSuiteId") Long testSuiteId, @Param("limit") Long limit);

    TestRunStatistics getTestRunStatistics(Long id);

    List<TestRun> getTestRunsForRerun(@Param("testSuiteId") long testSuiteId, @Param("jobId") long jobId, @Param("upstreamJobId") long upstreamJobId,
                                      @Param("upstreamBuildNumber") long upstreamBuildNumber, @Param("uniqueArgs") List<Argument> uniqueArgs);

    void updateTestRun(TestRun testRun);

    void reassignToProject(@Param("fromProjectId") Long fromProjectId, @Param("toProjectId") Long toProjectId);

    void deleteTestRunById(long id);

    void deleteTestRun(TestRun testRun);

    List<TestRun> getTestRunsByStatusAndStartedBefore(@Param("status") Status status, @Param("startedBefore") Date startedBefore);

    List<TestRun> searchTestRuns(TestRunSearchCriteria sc);

    List<TestRun> getTestRunsForSmartRerun(JobSearchCriteria sc);

    Integer getTestRunsSearchCount(TestRunSearchCriteria sc);

    Integer getTestRunEtaByTestSuiteId(long testRunId);

    List<TestRun> getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(@Param("jobId") Long jobId, @Param("buildNumber") Integer buildNumber);

    List<TestRun> getLatestJobTestRuns(@Param("env") String env, @Param("jobIds") List<Long> jobIds);

}
