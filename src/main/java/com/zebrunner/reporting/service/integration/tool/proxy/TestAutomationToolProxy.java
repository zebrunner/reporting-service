package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.AerokubeAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.BrowserStackAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.LambdaTestAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.MCloudAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.SauceLabsAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.SeleniumAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.ZebrunnerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestAutomationToolProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
        "SELENIUM", SeleniumAdapter.class,
        "ZEBRUNNER", ZebrunnerAdapter.class,
        "BROWSERSTACK", BrowserStackAdapter.class,
        "MCLOUD", MCloudAdapter.class,
        "SAUCELABS", SauceLabsAdapter.class,
        "AEROKUBE", AerokubeAdapter.class,
        "LAMBDATEST", LambdaTestAdapter.class
    );

    public TestAutomationToolProxy(
            ApplicationContext applicationContext,
            IntegrationGroupService integrationGroupService,
            IntegrationService integrationService,
            CryptoService cryptoService
    ) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "TEST_AUTOMATION_TOOL", INTEGRATION_TYPE_ADAPTERS);
    }
}
