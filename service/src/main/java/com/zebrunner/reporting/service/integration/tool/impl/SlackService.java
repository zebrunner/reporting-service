package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.TestRunService;
import com.zebrunner.reporting.service.email.TestRunResultsEmail;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.slack.SlackAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.SlackProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class SlackService extends AbstractIntegrationService<SlackAdapter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackService.class);

    private final static String ON_FINISH_PATTERN = "Test run #%1$d has been completed after %2$s with status %3$s\n";
    private final static String REVIEWED_PATTERN = "Test run #%1$d has been reviewed. Status: %2$s\n";

    private final IntegrationService integrationService;
    private final TestRunService testRunService;

    public SlackService(IntegrationService integrationService, SlackProxy slackProxy, TestRunService testRunService) {
        super(integrationService, slackProxy, "SLACK");
        this.integrationService = integrationService;
        this.testRunService = testRunService;
    }

    public void sendStatusOnFinish(String ciRunId, String channels) {
        TestRun testRun = testRunService.getTestRunByCiRunIdFull(ciRunId);
        // Triggered from Jenkins pipeline with the lis of channels passed in configXML
        //TODO by tsvrko on 2019-11-05: parse this field from configXML on testRun finish?
        testRun.setSlackChannels(channels);
        testRunService.updateTestRun(testRun);
        Integration slack = integrationService.retrieveDefaultByIntegrationTypeName("SLACK");
        if (slack.isEnabled()) {
            String readableTime = asReadableTime(testRun.getElapsed());
            String statusText = TestRunResultsEmail.buildStatusText(testRun);
            String onFinishMessage = String.format(ON_FINISH_PATTERN, testRun.getId(), readableTime, statusText);
            SlackAdapter adapter = getAdapterByIntegrationId(null);
            adapter.sendNotification(testRun, onFinishMessage);
        }
        // otherwise - do nothing
        LOGGER.info(String.format("Slack notification for test run %d is not sent: integration disabled", testRun.getId()));
    }

    public void sendStatusReviewed(long testRunId) {
        TestRun testRun = testRunService.getTestRunByIdFull(testRunId);
        String statusText = TestRunResultsEmail.buildStatusText(testRun);
        String reviewedMessage = String.format(REVIEWED_PATTERN, testRun.getId(), statusText);
        SlackAdapter adapter = getAdapterByIntegrationId(null);
        adapter.sendNotification(testRun, reviewedMessage);
    }

    public String getWebhook() {
        SlackAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.getWebhook();
    }

    /**
     * Converts to execution time in seconds to user-friendly form such as 10:13:33
     * @param elapsed elapsed time in seconds
     * @return formatted value
     */
    private String asReadableTime(Integer elapsed) {
        return elapsed != null ? LocalTime.ofSecondOfDay(elapsed).toString() : "";
    }

}
