package com.zebrunner.reporting.service.integration.tool.adapter;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIntegrationAdapter implements IntegrationAdapter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegrationAdapter.class);

    private final Long integrationId;

    public AbstractIntegrationAdapter(Integration integration) {
        this.integrationId = integration.getId();
    }

    @Override
    public Long getIntegrationId() {
        return integrationId;
    }

    protected static String getAttributeValue(Integration integration, AdapterParam adapterParam) {
        return integration.getAttributeValue(adapterParam.getName());
    }

    protected static byte[] getAttributeBinaryData(Integration integration, AdapterParam adapterParam) {
        return integration.getAttributeBinaryData(adapterParam.getName());
    }

}
