package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testcasemanagement.JiraIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testcasemanagement.QTestIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testcasemanagement.TestRailIntegrationAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestCaseManagementProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
            "JIRA", JiraIntegrationAdapter.class,
            "QTEST", QTestIntegrationAdapter.class,
            "TESTRAIL", TestRailIntegrationAdapter.class
    );

    public TestCaseManagementProxy(ApplicationContext applicationContext,
                                   IntegrationGroupService integrationGroupService,
                                   IntegrationService integrationService,
                                   CryptoService cryptoService) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "TEST_CASE_MANAGEMENT", INTEGRATION_TYPE_ADAPTERS);
    }
}
