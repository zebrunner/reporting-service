package com.zebrunner.reporting.service.email;

import com.zebrunner.reporting.domain.db.Attachment;
import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.TestRun;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TestRunResultsEmail implements IEmailMessage {
    private static final String SUBJECT = "%s: %s";

    private Map<String, String> customValues = new HashMap<>();
    private TestRun testRun;
    private List<Test> tests;
    private String jiraURL;
    private boolean showOnlyFailures = false;
    private boolean showStacktrace = true;
    private int successRate;
    private String elapsed;

    public TestRunResultsEmail(TestRun testRun, List<Test> tests) {
        this.testRun = testRun;
        this.tests = tests;
        this.elapsed = testRun.getElapsed() != null ? LocalTime.ofSecondOfDay(testRun.getElapsed()).toString() : null;
    }

    @Override
    public String getSubject() {
        String status = buildStatusText(testRun);
        return String.format(SUBJECT, status, testRun.getName());
    }

    public static String buildStatusText(TestRun testRun) {
        return Status.PASSED.equals(testRun.getStatus()) && testRun.isKnownIssue() && !testRun.isBlocker() ? "PASSED (known issues)"
                : testRun.isBlocker() ? "FAILED (BLOCKERS)" : testRun.getStatus().name();
    }

    @Override
    public EmailType getType() {
        return EmailType.TEST_RUN;
    }

    @Override
    public List<Attachment> getAttachments() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

}
