package com.zebrunner.reporting.service.integration.tool.adapter.notificationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import kong.unirest.Config;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.UnirestInstance;
import kong.unirest.UnirestParsingException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class MicrosoftTeamsIntegrationAdapter extends AbstractIntegrationAdapter implements NotificationServiceAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrosoftTeamsIntegrationAdapter.class);

    public static final String EMPTY_JSON_REQUEST_RESPONSE = "Summary or Text is required";

    public static final Map<String, String> NOTIFICATION_COLOR = Map.of("PASSED", "22CA88",
            "FAILED", "D43A40", "SKIPPED", "FFD276");

    private final String channelName;
    private final String webHookValue;

    private final UnirestInstance restClient;

    public MicrosoftTeamsIntegrationAdapter(Integration integration) {
        super(integration);

        String webHook = getAttributeValue(integration, MicrosoftTeamsParam.MICROSOFT_TEAMS_WEB_HOOK);
        Map<String, String> webhookData = getWebhookData(webHook);

        this.channelName = webhookData.get("channelName");
        this.webHookValue = webhookData.get("webHookValue");

        this.restClient = initClient();
    }

    private UnirestInstance initClient() {
        Config config = new Config();
        config.addDefaultHeader("Accept", "application/json");
        return new UnirestInstance(config);
    }

    private enum MicrosoftTeamsParam implements AdapterParam {
        MICROSOFT_TEAMS_WEB_HOOK("MICROSOFT_TEAMS_WEB_HOOK");

        private final String name;

        MicrosoftTeamsParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        boolean connected = false;
        try {
            if (webHookValue != null) {
                // Microsoft Teams webHook doesn't provide any functionality to check it's connectivity,
                // so, to make sure it is valid and avoid test messages in channels, we can push empty object and validate response.
                HttpResponse<JsonNode> response = restClient.post(webHookValue).body(new JsonObject()).asJson();
                // Regardless of the webhook itself or its payload is incorrect, response status is always 400
                if (response.getStatus() == 400) {
                    // So we should perform an extra check and the only source of it is a response message
                    UnirestParsingException responseMessage = response.getParsingError().orElse(null);
                    if (responseMessage != null) {
                        // We expect the message returned by API when valid webhook receives an empty Json payload we sent before,
                        // the other way the connection considered not established
                        connected = responseMessage.getOriginalBody().contains(EMPTY_JSON_REQUEST_RESPONSE);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return connected;
    }

    @Override
    public void sendNotification(TestRun testRun, Map<String, String> notificationProperties) {
        String channels = testRun.getChannels();
        if (StringUtils.isNotEmpty(channels)) {
            List<String> channelsList = Arrays.asList(channels.split(","));
            // Check that our single channel name is present in channels list
            if (channelIsNotPresent(channelsList)) {
                return;
            }
            JsonObject notification = generateMessageCard(testRun, notificationProperties);
            HttpResponse<JsonNode> response = restClient.post(webHookValue).body(notification).asJson();
            if (response.getStatus() != 200) {
                LOGGER.warn("Test run {} notification to teams about  is not sent", testRun.getId());
            }
        }
    }

    private boolean channelIsNotPresent(List<String> channelsList) {
        return channelsList.stream()
                           .noneMatch(channelName::equalsIgnoreCase);
    }

    @Override
    public String getWebhook() {
        return webHookValue;
    }

    /**
     * Generate MessageCard card which is the object of predefiend structure
     * accepted by Microsoft Teams webHook.
     *
     * @param testRun                - test run object to send notification about
     * @param notificationProperties - data present in every notification of notification service group.
     * @return MessageCard json ready for passing to webhook.
     */
    private JsonObject generateMessageCard(TestRun testRun, Map<String, String> notificationProperties) {
        JsonObject messageCard = new JsonObject();

        String testRunUpdateMessage = notificationProperties.get("testRunUpdateMessage");
        String testRunInfoMessage = notificationProperties.get("testRunInfoMessage");
        String serviceUrl = notificationProperties.get("serviceUrl");
        String jenkinsUrl = notificationProperties.get("jenkinsUrl");
        String notificationColor = NOTIFICATION_COLOR.get(testRun.getStatus().name());

        messageCard.addProperty("@context", "https://schema.org/extensions");
        messageCard.addProperty("@type", "MessageCard");
        messageCard.addProperty("themeColor", notificationColor);
        messageCard.addProperty("title", testRunUpdateMessage);
        messageCard.addProperty("text", testRunInfoMessage);
        messageCard.add("sections", getSections(testRun, serviceUrl, jenkinsUrl));

        return messageCard;
    }

    private JsonArray getSections(TestRun testRun, String serviceUrl, String jenkinsUrl) {
        JsonArray sections = new JsonArray();
        JsonObject sectionContent = new JsonObject();
        sectionContent.addProperty("text", "Test Results:");
        sectionContent.add("facts", getFacts(testRun));
        sectionContent.add("potentialAction", getPotentialActions(serviceUrl, jenkinsUrl));
        sections.add(sectionContent);
        return sections;
    }

    private JsonArray getFacts(TestRun testRun) {
        JsonArray facts = new JsonArray();
        facts.add(getFact("Passed:", testRun.getPassed().toString()));
        facts.add(getFact("Failed:", testRun.getFailed().toString()));
        facts.add(getFact("Known Issues:", testRun.getFailedAsKnown().toString()));
        facts.add(getFact("Skipped:", testRun.getSkipped().toString()));
        if (!isEmpty(testRun.getComments())) {
            facts.add(getFact("Comments:", String.format("**%s**", testRun.getComments())));
        }
        return facts;
    }

    private JsonObject getFact(String name, String value) {
        JsonObject fact = new JsonObject();
        fact.addProperty("name", name);
        fact.addProperty("value", value);
        return fact;
    }

    private JsonArray getPotentialActions(String serviceUrl, String jenkinsUrl) {
        JsonArray potentialActions = new JsonArray();
        potentialActions.add(getPotentialAction("Open in Zebrunner", serviceUrl));
        potentialActions.add(getPotentialAction("Open in Jenkins", jenkinsUrl));
        return potentialActions;
    }

    private JsonObject getPotentialAction(String linkName, String link) {
        JsonObject potentialAction = new JsonObject();
        potentialAction.addProperty("@type", "OpenUri");
        potentialAction.addProperty("name", linkName);
        JsonArray targets = new JsonArray();
        JsonObject target = getTarget(link);
        targets.add(target);
        potentialAction.add("targets", targets);
        return potentialAction;
    }

    private JsonObject getTarget(String serviceUrl) {
        JsonObject target = new JsonObject();
        target.addProperty("os", "default");
        target.addProperty("uri", serviceUrl);
        return target;
    }

    /**
     * Parse webHook value and if it is passed according to the pattern
     * channelName#webHookValue return map with channelName and webHookValue.
     *
     * @param webHook - webHook value passed by user
     * @return Map with channelName and webHookValue or empty map if value passed is incorrect.
     */
    private Map<String, String> getWebhookData(String webHook) {
        Map<String, String> hookData = new HashMap<>();
        String nameValueSeparator = "#";
        if (webHook != null) {
            String[] hookValues = webHook.split(nameValueSeparator);
            // Return values only if they are passed correctly
            if (hookValues.length > 1) {
                hookData.put("channelName", hookValues[0]);
                hookData.put("webHookValue", hookValues[1]);
            }
        }
        return hookData;
    }
}
