package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.List;

import com.zebrunner.reporting.domain.db.TestArtifact;
import org.apache.ibatis.annotations.Param;

public interface TestArtifactMapper {
    void createTestArtifact(TestArtifact testArtifact);

    TestArtifact getTestArtifactById(long id);

    List<TestArtifact> getTestArtifactsByTestId(long testId);

    TestArtifact getTestArtifactByNameAndTestId(@Param("name") String name, @Param("testId") long testId);

    void updateTestArtifact(TestArtifact testArtifact);

    void deleteTestArtifactById(long id);

    void deleteTestArtifactsByTestId(long testId);
}
