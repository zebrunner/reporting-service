package com.zebrunner.reporting.service.util;

import com.google.gson.Gson;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.domain.db.Invitation;
import com.zebrunner.reporting.domain.push.events.EmailEventMessage;
import com.zebrunner.reporting.domain.push.events.EventMessage;
import com.zebrunner.reporting.domain.push.events.TenancyResponseEventMessage;
import com.zebrunner.reporting.service.InvitationService;
import com.zebrunner.reporting.service.management.TenancyService;
import com.zebrunner.reporting.service.scm.ScmAccountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zebrunner.reporting.service.util.EventPushService.Type.TENANCIES;
import static com.zebrunner.reporting.service.util.EventPushService.Type.ZFR_CALLBACKS;

@Component
public class TenancyInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenancyInitializer.class);

    private static final String DEFAULT_USER_GROUP = "Admins";

    @Autowired
    private URLResolver urlResolver;

    @Autowired
    private TenancyService tenancyService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private ScmAccountService scmAccountService;

    @Autowired
    private EventPushService eventPushService;

    private final List<TenancyDbInitial> tenancyDbInitials;
    private final List<TenancyInitial> tenancyInitials;

    public TenancyInitializer(Map<String, TenancyDbInitial> tenancyDbInitials, Map<String, TenancyInitial> tenancyInitials) {
        this.tenancyDbInitials = new ArrayList<>(tenancyDbInitials.values());
        this.tenancyInitials = new ArrayList<>(tenancyInitials.values());
    }

    /**
     * RabbitMQ listener
     * 
     * @param message - amqp message
     */
    @RabbitListener(queues = "#{tenanciesQueue.name}")
    public void initTenancy(Message message) {
        try {
            EventMessage eventMessage = new Gson().fromJson(new String(message.getBody()), EventMessage.class);
            String tenancy = eventMessage.getTenantName();
            LOGGER.info("Tenancy '{}' initialization is started.", tenancy);
            initTenancy(tenancy);
            LOGGER.info("Tenancy '{}' initialization is finished.", tenancy);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "#{zfrEventsQueue.name}")
    public void initTenancyDb(Message message) {
        TenancyResponseEventMessage result;
        try {
            EmailEventMessage eventMessage = new Gson().fromJson(new String(message.getBody()), EmailEventMessage.class);
            String tenancy = eventMessage.getTenantName();
            result = new TenancyResponseEventMessage(tenancy);
            try {
                boolean tenancyDBInitCompleted = completeTenancyDBInitialization(tenancy);

                result.setSuccess(tenancyDBInitCompleted);

                createTenancyInvitation(result, eventMessage, tenancy);
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                result.setMessage(errorMessage);
                // Set success false whenever exception is thrown
                result.setSuccess(false);

                LOGGER.error(errorMessage, e);
            } finally {
                eventPushService.convertAndSend(ZFR_CALLBACKS, result, "Type", "ZFR_INIT_TENANCY");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private boolean completeTenancyDBInitialization(String tenancy) {
        LOGGER.info("Tenancy '{}' DB initialization is started.", tenancy);

        tenancyDbInitials.forEach(tenancyInitial -> initTenancyDb(tenancy, tenancyInitial));
        processMessage(tenancy, () -> scmAccountService.reEncryptTokens());

        boolean tenancyDBInitialized = eventPushService.convertAndSend(TENANCIES, new EventMessage(tenancy));

        LOGGER.info("Tenancy '{}' DB initialization is finished.", tenancy);

        return tenancyDBInitialized;
    }

    private void createTenancyInvitation(TenancyResponseEventMessage result, EmailEventMessage eventMessage, String tenancy) {
        processMessage(tenancy, () -> {
            try {
                LOGGER.info("Invitation to tenancy '{}' generation is started.", result.getTenantName());

                Invitation invitation = invitationService.createInitialInvitation(eventMessage.getEmail(), DEFAULT_USER_GROUP);
                result.setToken(invitation.getToken());
                result.setLogoUrl(urlResolver.buildWebURL());

                LOGGER.info("Invitation to tenancy '{}' generation is finished.", result.getTenantName());
            } catch (RuntimeException e) {
                String errorMessage = e.getMessage();
                result.setMessage(errorMessage);
                // Set success false whenever exception is thrown
                result.setSuccess(false);
                LOGGER.error(errorMessage, e);
            }
        });
    }

    private void initTenancy(String tenancy) {
        tenancyInitials.forEach(tenancyInitial -> initTenancy(tenancy, tenancyInitial));
    }

    /**
     * Trigger to execute some task on tenancy creation
     * 
     * @param tenancy - to initialize
     * @param tenancyInitial - task to execute
     */
    private void initTenancy(String tenancy, TenancyInitial tenancyInitial) {
        processMessage(tenancy, tenancyInitial::init);
    }

    private void initTenancyDb(String tenancy, TenancyDbInitial tenancyInitial) {
        processMessage(tenancy, tenancyInitial::initDb);
    }

    private void processMessage(String tenancy, Runnable runnable) {
        if (!StringUtils.isBlank(tenancy) && tenancyService.getTenancyByName(tenancy) != null) {
            TenancyContext.setTenantName(tenancy);
            runnable.run();
            TenancyContext.setTenantName(null);
        }
    }
}
