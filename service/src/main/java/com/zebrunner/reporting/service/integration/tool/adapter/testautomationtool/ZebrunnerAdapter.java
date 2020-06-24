package com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.util.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ZebrunnerAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String password;

    public ZebrunnerAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.password = getAttributeValue(integration, Parameter.PASSWORD);
    }

    @Override
    public String buildUrl() {
        return HttpUtils.buildBasicAuthUrl(url, username, password);
    }

    @Override
    public boolean isConnected() {
        return HttpUtils.isReachable(url, username, password, "", false) &&
                "/wd/hub".equals(HttpUtils.retrievePath(url));
    }

    @Getter
    @AllArgsConstructor
    private enum Parameter implements AdapterParam {
        URL("ZEBRUNNER_URL"),
        USERNAME("ZEBRUNNER_USER"),
        PASSWORD("ZEBRUNNER_PASSWORD");

        private final String name;
    }
}
