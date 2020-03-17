package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.automationserver.JenkinsIntegrationAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AutomationServerProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
            "JENKINS", JenkinsIntegrationAdapter.class
    );

    public AutomationServerProxy(ApplicationContext applicationContext,
                                 IntegrationGroupService integrationGroupService,
                                 CryptoService cryptoService,
                                 IntegrationService integrationService) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "AUTOMATION_SERVER", INTEGRATION_TYPE_ADAPTERS);
    }
}
