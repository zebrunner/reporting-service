package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.storageprovider.AmazonIntegrationAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StorageProviderProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
            "AMAZON", AmazonIntegrationAdapter.class
    );

    public StorageProviderProxy(ApplicationContext applicationContext,
                                @Value("${zafira.multitenant}") Boolean multitenant,
                                IntegrationGroupService integrationGroupService,
                                IntegrationService integrationService,
                                CryptoService cryptoService) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "STORAGE_PROVIDER", INTEGRATION_TYPE_ADAPTERS, Map.of("multitenant", multitenant));
    }
}
