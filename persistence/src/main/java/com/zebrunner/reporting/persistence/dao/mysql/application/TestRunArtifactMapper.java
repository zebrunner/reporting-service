package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.TestRunArtifact;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestRunArtifactMapper {

    void createTestRunArtifact(TestRunArtifact testRunArtifact);

    TestRunArtifact getTestRunArtifactByNameAndTestRunId(@Param("name") String name, @Param("testRunId") long testRunId);

}
