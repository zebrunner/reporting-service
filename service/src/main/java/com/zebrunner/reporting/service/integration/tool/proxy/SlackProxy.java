package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.slack.SlackIntegrationAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SlackProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
            "SLACK", SlackIntegrationAdapter.class
    );

    public SlackProxy(ApplicationContext applicationContext,
                      IntegrationGroupService integrationGroupService,
                      IntegrationService integrationService,
                      CryptoService cryptoService,
                      @Value("${zafira.slack.image-url}") String image,
                      @Value("${zafira.slack.author}") String author
    ) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "SLACK", INTEGRATION_TYPE_ADAPTERS, Map.of("image", image, "author", author));
    }
}
