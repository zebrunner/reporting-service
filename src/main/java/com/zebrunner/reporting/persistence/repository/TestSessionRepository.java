package com.zebrunner.reporting.persistence.repository;

import com.zebrunner.reporting.domain.entity.TestSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TestSessionRepository extends CrudRepository<TestSession, Long> {

    Optional<TestSession> findBySessionId(String sessionId);

    Page<TestSession> findAll(Specification<TestSession> specification, Pageable pageable);

    @Query("Select Distinct ts.browserName From TestSession ts")
    List<String> findDistinctByBrowserName();

    @Query("Select Distinct ts.status From TestSession ts")
    List<TestSession.Status> findDistinctByStatus();

}
