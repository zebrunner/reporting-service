package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.reporting.TestSession;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface TestSessionMapper {

    void create(TestSession session);

    TestSession findById(Long id);

    void update(TestSession session);

    void linkToTests(@Param("id") Long id, @Param("testRefs") Set<Long> testRefs);

}
