package com.zebrunner.reporting.service.reporting;

import com.zebrunner.reporting.domain.db.reporting.TestSession;
import com.zebrunner.reporting.persistence.dao.mysql.application.TestSessionMapper;
import com.zebrunner.reporting.service.TestService;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.TEST_SESSION_CAN_NOT_BE_UPDATED;

@Service
public class TestSessionServiceV1 {

    private static final String ERR_MSG_TEST_SESSION_NOT_EXIST_BY_ID = "Test session with id %d cannot be found";

    private final TestSessionMapper testSessionMapper;
    private final TestService testService;

    public TestSessionServiceV1(TestSessionMapper testSessionMapper, TestService testService) {
        this.testSessionMapper = testSessionMapper;
        this.testService = testService;
    }

    @Transactional
    public TestSession create(TestSession session) {
        Set<Long> existingRefs = getExistingRefs(session.getTestRefs());
        session.setTestRefs(existingRefs);
        testSessionMapper.create(session);
        testSessionMapper.linkToTests(session.getId(), session.getTestRefs());
        return session;
    }

    @Transactional(readOnly = true)
    public TestSession retrieveById(Long id) {
        TestSession session = testSessionMapper.findById(id);
        if (session == null) {
            throw new IllegalOperationException(TEST_SESSION_CAN_NOT_BE_UPDATED, String.format(ERR_MSG_TEST_SESSION_NOT_EXIST_BY_ID, session.getId()));
        }
        return session;
    }

    @Transactional
    public TestSession updateAndLinkToTests(TestSession session) {
        TestSession existingSession = retrieveById(session.getId());
        Set<Long> existingRefs = getExistingRefs(session.getTestRefs());
        session.setTestRefs(existingRefs);

        if (existingSession.getEndedAt() == null) {
            existingSession.setEndedAt(session.getEndedAt());
        }

        existingSession = update(existingSession);

        Set<Long> testRefsToAdd = mergeTestIds(existingSession.getTestRefs(), session.getTestRefs());
        testSessionMapper.linkToTests(existingSession.getId(), testRefsToAdd);

        return existingSession;
    }

    private Set<Long> getExistingRefs(Set<Long> testRefs) {
        Set<Long> existingRefs = null;
        if (testRefs != null) {
            existingRefs = testService.getExistingIds(testRefs);
        }
        return existingRefs;
    }

    private Set<Long> mergeTestIds(Set<Long> oldIds, Set<Long> newIds) {
        Set<Long> clonedNewIds = new HashSet<>(newIds);
        clonedNewIds.removeAll(oldIds);
        return clonedNewIds;
    }

    @Transactional
    public TestSession update(TestSession session) {
        TestSession existingSession = retrieveById(session.getId());
        existingSession.setEndedAt(session.getEndedAt());
        testSessionMapper.update(existingSession);
        return existingSession;
    }
}
