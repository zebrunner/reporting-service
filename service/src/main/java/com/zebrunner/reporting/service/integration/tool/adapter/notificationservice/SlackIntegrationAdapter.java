package com.zebrunner.reporting.service.integration.tool.adapter.notificationservice;

import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.integration.tool.impl.AutomationServerService;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import in.ashwanthkumar.slack.webhook.service.SlackService;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SlackIntegrationAdapter extends AbstractIntegrationAdapter implements NotificationServiceAdapter {

    private static final String ERR_MSG_SLACK_CONNECTION_IS_NOT_ESTABLISHED = "Slack connection is not established";
    public static final String ERR_MSG_UNABLE_TO_PUSH_SLACK_NOTIFICATION = "Unable to push Slack notification in channel {} ";

    private static final String RESULTS_PATTERN = "Passed: %d, Failed: %d, Known Issues: %d, Skipped: %d";
    private final static String INFO_PATTERN = "%1$s\n<%2$s|Open in Zafira>";
    private final static String OPEN_IN_JENKINS_INFO_PATTERN = "  |  <%1$s|Open in Jenkins>";

    public static final Map<String, String> NOTIFICATION_COLOR = Map.of("PASSED", "good", "FAILED", "danger", "SKIPPED", "warning");

    private final String image;
    private final String author;
    private final String webhookUrl;
    private final SlackService slackService;

    public SlackIntegrationAdapter(Integration integration,
                                   Map<String, String> additionalProperties, AutomationServerService automationServerService) {
        super(integration);

        this.image = additionalProperties.get("image");
        this.author = additionalProperties.get("author");

        this.webhookUrl = getAttributeValue(integration, SlackParam.SLACK_WEB_HOOK_URL);
        this.slackService = new SlackService();
    }

    private enum SlackParam implements AdapterParam {
        SLACK_WEB_HOOK_URL("SLACK_WEB_HOOK_URL");

        private final String name;

        SlackParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        return push(null, new SlackMessage(StringUtils.EMPTY), null);
    }

    @Override
    public void sendNotification(TestRun testRun, Map<String, String> notificationProperties) {
        String channels = testRun.getChannels();
        if (StringUtils.isNotEmpty(channels)) {
            SlackAttachment attachment = generateSlackAttachment(testRun, notificationProperties);
            Arrays.stream(channels.split(",")).forEach(channel -> {
                push(channel, new SlackMessage(), attachment);
            });
        }
    }

    @Override
    public String getWebhook() {
        return webhookUrl;
    }

    private SlackAttachment generateSlackAttachment(TestRun testRun, Map<String, String> notificationProperties) {
        String serviceUrl = notificationProperties.get("serviceUrl");
        String jenkinsUrl = notificationProperties.get("jenkinsUrl");
        String testRunUpdateMessage = notificationProperties.get("testRunUpdateMessage");
        String testRunInfoMessage = notificationProperties.get("testRunInfoMessage");
        String attachmentColor = NOTIFICATION_COLOR.get(testRun.getStatus().name());

        String mainMessage = testRunUpdateMessage + String.format(INFO_PATTERN, testRunInfoMessage, serviceUrl);
        if (jenkinsUrl != null) {
            mainMessage += String.format(OPEN_IN_JENKINS_INFO_PATTERN, jenkinsUrl);
        }
        String resultsMessage = String.format(RESULTS_PATTERN, testRun.getPassed(), testRun.getFailed(), testRun.getFailedAsKnown(), testRun.getSkipped());

        SlackAttachment slackAttachment = new SlackAttachment("");
        slackAttachment
                .preText(mainMessage)
                .color(attachmentColor)
                .addField(new SlackAttachment.Field("Test Results", resultsMessage, false))
                .fallback(mainMessage + "\n" + resultsMessage);

        if (testRun.getComments() != null) {
            slackAttachment.addField(new SlackAttachment.Field("Comments", testRun.getComments(), false));
        }
        return slackAttachment;
    }

    private boolean push(String channel, SlackMessage message, SlackAttachment slackAttachment) {
        boolean isConnected = false;
        List<SlackAttachment> attachments = slackAttachment != null ? List.of(slackAttachment) : Collections.emptyList();
        try {
            slackService.push(webhookUrl, message, author, image, channel, null, attachments);
        } catch (IOException e) {
            if (e.getMessage().contains("400")) {
                isConnected = true;
            }
            LOGGER.error(ERR_MSG_UNABLE_TO_PUSH_SLACK_NOTIFICATION, channel);
        } catch (Exception e) {
            LOGGER.error(ERR_MSG_SLACK_CONNECTION_IS_NOT_ESTABLISHED + "\n" + e.getMessage());
        }
        return isConnected;
    }

}
