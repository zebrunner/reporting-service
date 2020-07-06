package com.zebrunner.reporting.service.integration.core;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.zebrunner.reporting.domain.push.events.EventMessage;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.push.events.ReinitEventMessage;
import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.IntegrationSettingService;
import com.zebrunner.reporting.service.integration.tool.proxy.IntegrationAdapterProxy;
import com.zebrunner.reporting.service.management.TenancyService;
import com.zebrunner.reporting.service.util.EventPushService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
@DependsOn("databaseStateManager")
@RequiredArgsConstructor
public class IntegrationTenancyStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTenancyStorage.class);

    private final TenancyService tenancyService;
    private final IntegrationService integrationService;
    private final IntegrationInitializer integrationInitializer;
    private final IntegrationSettingService integrationSettingService;
    private final EventPushService<EventMessage> eventPushService;
    private final Map<String, IntegrationAdapterProxy> integrationProxies;
    private final CryptoService cryptoService;

    @PostConstruct
    public void post() {
        tenancyService.iterateItems(() -> {
            encryptIntegrationSettings();
            initIntegrationProxies();
        });
    }

    public void initIntegrationProxies() {
        integrationProxies.forEach((name, proxy) -> proxy.init());
    }

    public void encryptIntegrationSettings() {
        try {
            cryptoService.init();
            List<Integration> integrations = integrationService.retrieveAll();
            integrations.forEach(integration -> integration.getSettings().forEach(integrationSetting -> {
                if (!StringUtils.isEmpty(integrationSetting.getValue()) && integrationSetting.getParam().isNeedEncryption() && !integrationSetting.isEncrypted()) {
                    integrationSetting.setValue(cryptoService.encrypt(integrationSetting.getValue()));
                    integrationSetting.setEncrypted(true);
                    integrationSettingService.update(integrationSetting);
                }
            }));
        } catch (Exception e) {
            LOGGER.error("Unable to encrypt value: " + e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "#{settingsQueue.name}")
    public void process(Message message) {
        try {
            ReinitEventMessage event = new Gson().fromJson(new String(message.getBody()), ReinitEventMessage.class);

            long integrationId = event.getIntegrationId();
            String tenantName = event.getTenantName();
            try {
                if (!eventPushService.isSettingQueueConsumer(message)) {
                    TenancyContext.setTenantName(tenantName);

                    Integration integration = integrationService.retrieveById(integrationId);
                    integrationInitializer.initIntegration(integration, tenantName);

                    TenancyContext.setTenantName(null);
                }
            } catch (Exception e) {
                LOGGER.error(String.format("Unable to initialize adapter for integration with id %d. ", integrationId) + e.getMessage(), e);
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            LOGGER.error("Unable to map even message to ReinitEventMessage type. " + e.getMessage(), e);
        }
    }

}
