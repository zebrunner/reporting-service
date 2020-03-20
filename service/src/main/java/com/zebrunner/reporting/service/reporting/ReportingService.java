package com.zebrunner.reporting.service.reporting;

import com.zebrunner.reporting.domain.db.reporting.Test;
import com.zebrunner.reporting.domain.db.reporting.TestRun;
import com.zebrunner.reporting.service.TestRunService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportingService {

    private final TestRunService testRunService;

    @Transactional
    public TestRun startRun(TestRun testRun) {

    }

    @Transactional
    public TestRun finishRun(TestRun testRun) {

    }

    @Transactional
    public Test startTest(Test test) {

    }

    @Transactional
    public Test finishTest(Test test) {

    }
}
