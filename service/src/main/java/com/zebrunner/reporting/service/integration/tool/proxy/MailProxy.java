package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.mail.MailIntegrationAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MailProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
            "EMAIL", MailIntegrationAdapter.class
    );

    public MailProxy(ApplicationContext applicationContext,
                     IntegrationGroupService integrationGroupService,
                     IntegrationService integrationService,
                     CryptoService cryptoService) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "MAIL", INTEGRATION_TYPE_ADAPTERS);
    }

}
