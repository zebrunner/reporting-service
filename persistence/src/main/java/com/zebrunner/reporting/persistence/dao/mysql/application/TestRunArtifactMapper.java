package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.TestRunArtifact;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestRunArtifactMapper {

    void createTestRunArtifact(TestRunArtifact testRunArtifact);

    TestRunArtifact getTestRunArtifactById(long id);

    List<TestRunArtifact> getTestRunArtifactsByTestRunId(long testRunId);

    TestRunArtifact getTestRunArtifactByNameAndTestRunId(@Param("name") String name, @Param("testRunId") long testRunId);

    void updateTestRunArtifact(TestRunArtifact testRunArtifact);

    void deleteTestRunArtifactById(long id);

    void deleteTestRunArtifactsByTestRunId(long testRunId);
}
