package com.zebrunner.reporting.service.integration.core;

import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.entity.integration.IntegrationGroup;
import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import com.zebrunner.reporting.service.exception.IntegrationException;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.proxy.IntegrationAdapterProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IntegrationInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationInitializer.class);

    private static final String INITIALIZING_INTEGRATION_BY_TYPE_START = "Initializing integration %s of type %s";
    private static final String ERR_MSG_GROUP_NOT_EXISTS = "Integration group with name %s does not exist";

    private final Map<String, IntegrationAdapterProxy> integrationProxies;
    private final Map<String, AbstractIntegrationService> integrationServices;
    private final IntegrationGroupService integrationGroupService;

    public IntegrationInitializer(
            @Lazy Map<String, IntegrationAdapterProxy> integrationProxies,
            @Lazy Map<String, AbstractIntegrationService> integrationServices,
            IntegrationGroupService integrationGroupService
    ) {
        this.integrationProxies = integrationProxies;
        this.integrationServices = integrationServices;
        this.integrationGroupService = integrationGroupService;
    }

    public void initIntegration(Integration integration, String tenant) {
        IntegrationType type = integration.getType();
        IntegrationGroup group = integrationGroupService.retrieveByIntegrationTypeId(type.getId());

        TenancyContext.setTenantName(tenant);
        initByType(group.getName(), type.getName(), integration);
        // TODO by nsidorevich on 2019-10-01: we need to better understand why we set it to null
//        TenancyContext.setTenantName(null);
    }

    private void initByType(String group, String type, Integration integration) {
        LOGGER.info(String.format(INITIALIZING_INTEGRATION_BY_TYPE_START, integration.getName(), type));

        IntegrationAdapterProxy adapterProxy = integrationProxies.values().stream()
                                                                 .filter(integrationAdapterProxy -> integrationAdapterProxy.getGroup().equals(group))
                                                                 .findFirst()
                                                                 .orElseThrow(() -> new IntegrationException(String.format(ERR_MSG_GROUP_NOT_EXISTS, group)));

        adapterProxy.initializeByType(type, List.of(integration));
    }

    public Map<String, AbstractIntegrationService> getIntegrationServices() {
        return integrationServices.entrySet().stream()
                                  .collect(Collectors.toMap(entry -> entry.getValue().getIntegrationAdapterProxy().getGroup(), Map.Entry::getValue));
    }
}
