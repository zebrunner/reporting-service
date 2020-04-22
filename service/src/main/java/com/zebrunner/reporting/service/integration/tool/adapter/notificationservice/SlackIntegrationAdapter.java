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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class SlackIntegrationAdapter extends AbstractIntegrationAdapter implements NotificationServiceAdapter {

    private static final String ERR_MSG_SLACK_CONNECTION_IS_NOT_ESTABLISHED = "Slack connection is not established";

    private static final String RESULTS_PATTERN = "Passed: %d, Failed: %d, Known Issues: %d, Skipped: %d";
    private final static String INFO_PATTERN = "%1$s\n<%2$s|Open in Zafira>";
    private final static String OPEN_IN_JENKINS_INFO_PATTERN = "  |  <%1$s|Open in Jenkins>";

    public static final Map<String, String> NOTIFICATION_COLOR = Map.of("PASSED", "good", "FAILED", "danger", "SKIPPED", "warning");

    private final String image;
    private final String author;
    private final String webhookUrl;
    private final SlackService slackService;

    public SlackIntegrationAdapter(Integration integration,
                                   Map<String, String> additionalProperties) {
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
        boolean result;
        try {
            /*
            Slack webHook doesn't provide any functionality to check it's connectivity,
            so, to make sure it is valid and avoid test messages in channels,
            we can push empty message with null channel name and attachment
            */
            push(null, new SlackMessage(StringUtils.EMPTY));
            result = true;
        } catch (IOException e) {
            LOGGER.error(ERR_MSG_SLACK_CONNECTION_IS_NOT_ESTABLISHED + "\n" + e.getMessage());
             /*
             Based on slackService functionality method push is void and in case of 4** response
             throws exception with message containing status code.
             400 is expected status code in case when valid webhook receives null message
             which we on purpose send checking connectivity
             */
            result = e.getMessage().contains("400");
        } catch (Exception e) {
            LOGGER.error(ERR_MSG_SLACK_CONNECTION_IS_NOT_ESTABLISHED, e);
            result = false;
        }
        return result;
    }

    @Override
    public void sendNotification(TestRun testRun, Map<String, String> notificationProperties) {
        String channels = testRun.getChannels();
        if (StringUtils.isNotEmpty(channels)) {
            SlackAttachment attachment = generateSlackAttachment(testRun, notificationProperties);
            Arrays.stream(channels.split(",")).forEach(channel -> {
                try {
                    push(channel, attachment);
                } catch (IOException e) {
                    LOGGER.error("Unable to push Slack notification");
                }
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
        slackAttachment.preText(mainMessage)
                       .color(attachmentColor)
                       .addField(new SlackAttachment.Field("Test Results", resultsMessage, false))
                       .fallback(mainMessage + "\n" + resultsMessage);

        if (testRun.getComments() != null) {
            slackAttachment.addField(new SlackAttachment.Field("Comments", testRun.getComments(), false));
        }
        return slackAttachment;
    }

    private void push(String channel, SlackAttachment slackAttachment) throws IOException {
        slackService.push(webhookUrl, new SlackMessage(), author, image, channel, null, Collections.singletonList(slackAttachment));
    }

    private void push(String channel, SlackMessage message) throws IOException {
        slackService.push(webhookUrl, message, author, image, channel, null, new ArrayList<>());
    }

}
