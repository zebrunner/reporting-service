package com.zebrunner.reporting.service.integration.tool.adapter.slack;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.zebrunner.reporting.domain.db.TestConfig;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.util.URLResolver;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import in.ashwanthkumar.slack.webhook.service.SlackService;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

public class SlackIntegrationAdapter extends AbstractIntegrationAdapter implements SlackAdapter {

    private static final String ERR_MSG_SLACK_CONNECTION_IS_NOT_ESTABLISHED = "Slack connection is not established";

    private final static String RESULTS_PATTERN = "Passed: %d, Failed: %d, Known Issues: %d, Skipped: %d";
    private final static String INFO_PATTERN = "%1$s\n<%2$s|Open in Zafira>  |  <%3$s|Open in Jenkins>";

    private final String image;
    private final String author;
    private final String webhookUrl;
    private final SlackService slackService;
    private final URLResolver urlResolver;

    public SlackIntegrationAdapter(Integration integration,
                                   URLResolver urlResolver,
                                   Map<String, String> additionalProperties) {
        super(integration);

        this.urlResolver = urlResolver;

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
            push(null, new SlackMessage(StringUtils.EMPTY));
            result = true;
        } catch (HttpResponseException e) {
            LOGGER.error(ERR_MSG_SLACK_CONNECTION_IS_NOT_ESTABLISHED, e);
            result = e.getStatusCode() != HttpStatusCodes.STATUS_CODE_NOT_FOUND;
        } catch (Exception e) {
            LOGGER.error(ERR_MSG_SLACK_CONNECTION_IS_NOT_ESTABLISHED, e);
            result = false;
        }
        return result;
    }

    @Override
    public void sendNotification(TestRun tr, String customizedMessage) {
        String channels = tr.getSlackChannels();
        if (StringUtils.isNotEmpty(channels)) {
            String zafiraUrl = urlResolver.buildWebURL() + "/tests/runs/" + tr.getId();
            String jenkinsUrl = tr.getJob().getJobURL() + "/" + tr.getBuildNumber();
            String attachmentColor = determineColor(tr);
            String mainMessage = customizedMessage + String.format(INFO_PATTERN, buildRunInfo(tr), zafiraUrl, jenkinsUrl);
            String resultsMessage = String.format(RESULTS_PATTERN, tr.getPassed(), tr.getFailed(), tr.getFailedAsKnown(), tr.getSkipped());
            SlackAttachment attachment = generateSlackAttachment(mainMessage, resultsMessage, attachmentColor, tr.getComments());
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

    private String determineColor(TestRun tr) {
        if (tr.getPassed() > 0 && tr.getFailed() == 0 && tr.getSkipped() == 0) {
            return "good";
        }
        if (tr.getPassed() == 0 && tr.getFailed() == 0 && tr.getFailedAsKnown() == 0
                && tr.getSkipped() == 0) {
            return "danger";
        }
        return "warning";
    }

    private String buildRunInfo(TestRun testRun) {
        StringBuilder testRunInfo = new StringBuilder();
        Map<String, String> jenkinsParams = testRun.getConfiguration();
        TestConfig config = testRun.getConfig();
        testRunInfo.append(testRun.getProject().getName());
        if (jenkinsParams != null && jenkinsParams.get("groups") != null) {
            testRunInfo.append("(")
                       .append(jenkinsParams.get("groups"))
                       .append(")");
        }
        testRunInfo.append(" | ")
                   .append(testRun.getTestSuite().getName())
                   .append(" | ")
                   .append(testRun.getConfig().getEnv())
                   .append(" | ")
                   .append(isEmpty(config.buildPlatformName()) ? "no_platform" : config.buildPlatformName());
        if (config.getAppVersion() != null) {
            testRunInfo.append(" | ")
                       .append(config.getAppVersion());
        }
        return testRunInfo.toString();
    }

    private SlackAttachment generateSlackAttachment(String mainMessage, String messageResults, String attachmentColor, String comments) {
        SlackAttachment slackAttachment = new SlackAttachment("");
        slackAttachment
                .preText(mainMessage)
                .color(attachmentColor)
                .addField(new SlackAttachment.Field("Test Results", messageResults, false))
                .fallback(mainMessage + "\n" + messageResults);
        if (comments != null) {
            slackAttachment.addField(new SlackAttachment.Field("Comments", comments, false));
        }
        return slackAttachment;
    }

    private void push(String channel, SlackAttachment slackAttachment) throws IOException {
        slackService.push(webhookUrl, new SlackMessage(), author, image, channel, null, Collections.singletonList(slackAttachment));
    }

    private void push(String channel, SlackMessage message) throws IOException {
        slackService.push(webhookUrl, message, author, image, channel, null, new ArrayList<>());
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public SlackService getSlackService() {
        return slackService;
    }

}
