package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.messagebroker.RabbitMQIntegrationAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageBrokerProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
            "RABBITMQ", RabbitMQIntegrationAdapter.class
    );

    public MessageBrokerProxy(ApplicationContext applicationContext,
                              IntegrationGroupService integrationGroupService,
                              IntegrationService integrationService,
                              CryptoService cryptoService){
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "MESSAGE_BROKER", INTEGRATION_TYPE_ADAPTERS);
    }
}
