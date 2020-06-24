package com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.util.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SeleniumAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String password;

    public SeleniumAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.password = getAttributeValue(integration, Parameter.PASSWORD);
    }

    @Override
    public boolean isConnected() {
        return HttpUtils.isReachable(url, username, password, "/status", false);
    }

    @Override
    public String buildUrl() {
        return HttpUtils.buildBasicAuthUrl(url, username, password);
    }

    @Getter
    @AllArgsConstructor
    private enum Parameter implements AdapterParam {
        URL("SELENIUM_URL"),
        USERNAME("SELENIUM_USER"),
        PASSWORD("SELENIUM_PASSWORD");

        private final String name;
    }

}
