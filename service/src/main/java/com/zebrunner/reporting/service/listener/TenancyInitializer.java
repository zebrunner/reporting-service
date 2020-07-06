package com.zebrunner.reporting.service.listener;

import com.google.gson.Gson;
import com.zebrunner.reporting.domain.push.events.EmailEventMessage;
import com.zebrunner.reporting.domain.push.events.EventMessage;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.service.email.CreateDefaultUserMessage;
import com.zebrunner.reporting.service.integration.core.IntegrationTenancyStorage;
import com.zebrunner.reporting.service.management.TenancyService;
import com.zebrunner.reporting.service.scm.ScmAccountService;
import com.zebrunner.reporting.service.util.EventPushService;
import com.zebrunner.reporting.service.util.URLResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.zebrunner.reporting.service.ExchangeConfig.CREATE_DEFAULT_USER_EXCHANGE;
import static com.zebrunner.reporting.service.ExchangeConfig.CREATE_DEFAULT_USER_ROUTING_KEY;
import static com.zebrunner.reporting.service.util.EventPushService.Routing.TENANCIES;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenancyInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenancyInitializer.class);

    private final URLResolver urlResolver;
    private final TenancyService tenancyService;
    private final ScmAccountService scmAccountService;
    private final EventPushService<EventMessage> eventPushService;
    private final IntegrationTenancyStorage integrationTenancyStorage;
    private final MessageHelper messageHelper;

    /**
     * RabbitMQ listener
     *
     * @param message - amqp message
     */
    @RabbitListener(queues = "#{tenanciesQueue.name}")
    public void initTenancy(Message message) {
        try {
            EventMessage eventMessage = messageHelper.parse(message, EventMessage.class);
            String tenancy = eventMessage.getTenantName();

            log.info("Tenancy '{}' initialization is started.", tenancy);

            processMessage(tenancy, integrationTenancyStorage::initIntegrationProxies);

            log.info("Tenancy '{}' initialization is finished.", tenancy);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "#{zfrEventsQueue.name}")
    public void initTenancyDb(Message message) {
        EmailEventMessage eventMessage = messageHelper.parse(message, EmailEventMessage.class);
        String tenancy = eventMessage.getTenantName();
        CreateDefaultUserMessage createDefaultUserMessage = new CreateDefaultUserMessage(tenancy,
                urlResolver.buildWebURL(),
                eventMessage.getEmail(), true, "");
        try {
            log.info("Tenancy '{}' DB initialization is started.", tenancy);

            processMessage(tenancy, integrationTenancyStorage::encryptIntegrationSettings);
            processMessage(tenancy, scmAccountService::encryptTokens);

            eventPushService.convertAndSend(TENANCIES, new EventMessage(tenancy));

            log.info("Tenancy '{}' DB initialization is finished.", tenancy);

        } catch (Exception e) {
            log.error("Tenancy '{}' DB initialization failed\n" + e.getMessage(), tenancy);

            createDefaultUserMessage.setSuccess(false);
            createDefaultUserMessage.setMessage(e.getMessage());

        } finally {
            eventPushService.send(CREATE_DEFAULT_USER_EXCHANGE, CREATE_DEFAULT_USER_ROUTING_KEY,
                    createDefaultUserMessage, Map.of("Type", "CREATE_DEFAULT_USER"));
        }
    }

    private void processMessage(String tenancy, Runnable runnable) {
        if (!StringUtils.isBlank(tenancy) && tenancyService.getTenancyByName(tenancy) != null) {
            TenancyContext.setTenantName(tenancy);
            runnable.run();
            TenancyContext.setTenantName(null);
        }
    }
}
