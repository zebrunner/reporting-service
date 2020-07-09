package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.notificationservice.MicrosoftTeamsIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.notificationservice.SlackIntegrationAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationServiceProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
            "SLACK", SlackIntegrationAdapter.class,
            "MICROSOFT_TEAMS", MicrosoftTeamsIntegrationAdapter.class);

    public NotificationServiceProxy(ApplicationContext applicationContext,
                                    IntegrationGroupService integrationGroupService,
                                    IntegrationService integrationService,
                                    CryptoService cryptoService,
                                    @Value("${slack.image-url}") String image,
                                    @Value("${slack.author}") String author
    ) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "NOTIFICATION_SERVICE", INTEGRATION_TYPE_ADAPTERS, Map.of("image", image, "author", author));
    }
}
