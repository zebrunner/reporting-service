package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.TestAutomationToolAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.TestAutomationToolProxy;
import org.springframework.stereotype.Component;

@Component
public class TestAutomationToolService extends AbstractIntegrationService<TestAutomationToolAdapter> {

    public TestAutomationToolService(IntegrationService integrationService, TestAutomationToolProxy testAutomationToolProxy) {
        super(integrationService, testAutomationToolProxy, "SELENIUM");
    }

    public String buildUrl(Long integrationId) {
        TestAutomationToolAdapter adapter = getAdapterByIntegrationId(integrationId);
        return adapter.buildUrl();
    }

}
