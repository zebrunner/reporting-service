package com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.util.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class BrowserStackAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private static final String HEALTH_CHECK_PATH = "https://api.browserstack.com/automate/plan.json/";

    private final String url;
    private final String username;
    private final String accessKey;

    public BrowserStackAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.accessKey = getAttributeValue(integration, Parameter.ACCESS_KEY);
    }

    @Override
    public String buildUrl() {
        return HttpUtils.buildBasicAuthUrl(url, username, accessKey);
    }

    @Override
    public boolean isConnected() {
        return HttpUtils.isReachable(HEALTH_CHECK_PATH, username, accessKey, "", false) &&
                HttpUtils.isReachable(url, username, accessKey, "/status", false);
    }

    @Getter
    @AllArgsConstructor
    private enum Parameter implements AdapterParam {
        URL("BROWSERSTACK_URL"),
        USERNAME("BROWSERSTACK_USER"),
        ACCESS_KEY("BROWSERSTACK_ACCESS_KEY");

        private final String name;
    }
}
