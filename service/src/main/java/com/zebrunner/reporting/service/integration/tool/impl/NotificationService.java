package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.domain.db.TestConfig;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import com.zebrunner.reporting.service.TestRunService;
import com.zebrunner.reporting.service.email.TestRunResultsEmail;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.IntegrationTypeService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.notificationservice.NotificationServiceAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.NotificationServiceProxy;
import com.zebrunner.reporting.service.util.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

@Component
public class NotificationService extends AbstractIntegrationService<NotificationServiceAdapter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final static String ON_FINISH_PATTERN = "Test run #%1$d has been completed after %2$s with status %3$s\n";
    private final static String REVIEWED_PATTERN = "Test run #%1$d has been reviewed. Status: %2$s\n";

    private final IntegrationService integrationService;
    private final IntegrationTypeService integrationTypeService;
    private final AutomationServerService automationServerService;
    private final TestRunService testRunService;
    private final URLResolver urlResolver;

    public NotificationService(NotificationServiceProxy notificationServiceProxy,
                               IntegrationService integrationService,
                               IntegrationTypeService integrationTypeService,
                               AutomationServerService automationServerService,
                               TestRunService testRunService,
                               URLResolver urlResolver) {
        super(integrationService, notificationServiceProxy, "SLACK");
        this.integrationService = integrationService;
        this.integrationTypeService = integrationTypeService;
        this.automationServerService = automationServerService;
        this.testRunService = testRunService;
        this.urlResolver = urlResolver;
    }

    /**
     * Triggered from Jenkins pipeline with the list of channels passed in configXML.
     * Send notifications to provided list of channels.
     *
     * @param ciRunId  - test run ci run id
     * @param channels - comma-separated list of channels
     */
    public void sendStatusOnFinish(String ciRunId, String channels) {
        TestRun testRun = updateTestRunChannels(ciRunId, channels);
        String readableTime = asReadableTime(testRun.getElapsed());
        String statusText = TestRunResultsEmail.buildStatusText(testRun);
        String onFinishMessage = String.format(ON_FINISH_PATTERN, testRun.getId(), readableTime, statusText);
        sendNotifications(testRun, onFinishMessage);
    }

    /**
     * Send notifications to testrun channels when testRun is reviewed.
     *
     * @param testRunId - id ot testrun to send review notification about.
     */
    public void sendStatusOnReview(long testRunId) {
        TestRun testRun = testRunService.getTestRunByIdFull(testRunId);
        String statusText = TestRunResultsEmail.buildStatusText(testRun);
        String reviewedMessage = String.format(REVIEWED_PATTERN, testRun.getId(), statusText);
        sendNotifications(testRun, reviewedMessage);
    }

    /**
     * Sends notifications to all integrations present in group
     *
     * @param testRun - test run to send notification about
     * @param message - message to send
     */
    private void sendNotifications(TestRun testRun, String message) {
        Map<String, String> notificationProperties = getNotificationProperties(testRun, message);

        IntegrationType defaultType = integrationTypeService.retrieveByName(getDefaultType());
        List<Integration> integrations = integrationService.retrieveIntegrationsByGroupId(defaultType.getGroup().getId());

        integrations.forEach(integration -> {
            if (integration.isEnabled()) {
                try {
                    NotificationServiceAdapter adapter = getAdapterByIntegrationId(integration.getId());
                    adapter.sendNotification(testRun, notificationProperties);
                } catch (IllegalOperationException e) {
                    LOGGER.error(e.getMessage());
                }
            } else {
                LOGGER.info(String.format("Notification for test run %d is not sent: integration disabled", testRun.getId()));
            }
        });
    }

    public String getWebhook(Long integrationId) {
        NotificationServiceAdapter adapter = integrationId == null ? getDefaultAdapterByType() : getAdapterByIntegrationId(integrationId);
        return adapter.getWebhook();
    }

    private TestRun updateTestRunChannels(String ciRunId, String channels) {
        TestRun testRun = testRunService.getTestRunByCiRunIdFull(ciRunId);
        testRun.setChannels(channels);
        testRunService.updateTestRun(testRun);
        return testRun;
    }

    private Map<String, String> getNotificationProperties(TestRun testRun, String message) {
        Map<String, String> notificationProperties = new HashMap<>();
        notificationProperties.put("testRunUpdateMessage", message);

        String testRunInfoMessage = generateTestRunInfo(testRun);
        notificationProperties.put("testRunInfoMessage", testRunInfoMessage);

        String serviceUrl = urlResolver.buildWebURL() + "/tests/runs/" + testRun.getId();
        notificationProperties.put("serviceUrl", serviceUrl);

        Long automationServerId = testRun.getJob().getAutomationServerId();
        boolean showJobUrl = automationServerService.showJobUrl(automationServerId);
        if (showJobUrl) {
            String jenkinsUrl = testRun.getJob().getJobURL() + "/" + testRun.getBuildNumber();
            notificationProperties.put("jenkinsUrl", jenkinsUrl);
        }

        return notificationProperties;
    }

    /**
     * Concatenate in single line test run characteristics (e.g. name, env, platform)
     * using ' | ' as separator.
     *
     * @param testRun - test run object
     * @return String value containing information about test run
     */
    private String generateTestRunInfo(TestRun testRun) {
        StringBuilder testRunInfo = new StringBuilder();
        String separator = " | ";
        Map<String, String> jenkinsParams = testRun.getConfiguration();
        TestConfig config = testRun.getConfig();
        testRunInfo.append(testRun.getProject().getName());
        if (jenkinsParams != null && jenkinsParams.get("groups") != null) {
            testRunInfo.append("(")
                       .append(jenkinsParams.get("groups"))
                       .append(")");
        }
        testRunInfo.append(separator)
                   .append(testRun.getTestSuite().getName())
                   .append(separator)
                   .append(testRun.getConfig().getEnv())
                   .append(separator)
                   .append(isEmpty(config.buildPlatformName()) ? "no platform" : config.buildPlatformName());
        if (!isEmpty(config.getAppVersion())) {
            testRunInfo.append(separator)
                       .append(config.getAppVersion());
        }
        if (!isEmpty(config.getLocale())) {
            testRunInfo.append(separator)
                       .append(config.getLocale());
        }
        return testRunInfo.toString();
    }

    /**
     * Converts to execution time in seconds to user-friendly form such as 10:13:33
     *
     * @param elapsed elapsed time in seconds
     * @return formatted value
     */
    private String asReadableTime(Integer elapsed) {
        return elapsed != null ? LocalTime.ofSecondOfDay(elapsed).toString() : "";
    }

}
