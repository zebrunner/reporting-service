package com.zebrunner.reporting.service.integration.tool;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.IntegrationException;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.IntegrationAdapterProxy;

import java.util.Optional;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.INTEGRATION_USAGE_NOT_POSSIBLE;

public abstract class AbstractIntegrationService<T extends IntegrationGroupAdapter> {

    private static final String ERR_MSG_ADAPTER_NOT_FOUND = "Requested adapter with id %d can not be found";
    private static final String ERR_MSG_ADAPTER_NOT_FOUND_BY_TYPE = "Requested adapter of type %s can not be found";
    private static final String ERR_MSG_INTEGRATION_USAGE_NOT_POSSIBLE = "Requested adapter with id %d can not be used. It is not enabled or connected";

    private final IntegrationService integrationService;
    private final IntegrationAdapterProxy integrationAdapterProxy;
    private final String defaultType;

    public AbstractIntegrationService(IntegrationService integrationService, IntegrationAdapterProxy integrationAdapterProxy, String defaultType) {
        this.integrationService = integrationService;
        this.integrationAdapterProxy = integrationAdapterProxy;
        this.defaultType = defaultType;
    }

    public boolean isEnabledAndConnected(Long integrationId) {
        Optional<IntegrationAdapter> maybeAdapter = getAdapter(integrationId);
        if (maybeAdapter.isEmpty()) {
            return false;
        }

        IntegrationAdapter adapter = maybeAdapter.get();
        // adapter presence is already verified
        return isEnabledAndConnected(adapter);
    }

    private boolean isEnabledAndConnected(IntegrationAdapter adapter) {
        Long adapterIntegrationId = adapter.getIntegrationId();
        // we now use proper integration id since it can be null prior to this point, but never for adapter
        Integration integration = integrationService.retrieveById(adapterIntegrationId);

        return integration.isEnabled() && adapter.isConnected();
    }

    @SuppressWarnings("unchecked")
    public T getAdapterByIntegrationId(Long integrationId) {
        IntegrationAdapter adapter = (IntegrationAdapter) getNotNullAdapterByIntegrationId(integrationId);
        boolean isAdapterEnabledAndConnected = isEnabledAndConnected(adapter);
        if (!isAdapterEnabledAndConnected) {
            throw new IllegalOperationException(INTEGRATION_USAGE_NOT_POSSIBLE, String.format(ERR_MSG_INTEGRATION_USAGE_NOT_POSSIBLE, integrationId));
        }
        return (T) adapter;
    }

    public T getDefaultAdapterByType() {
        return getAdapterByIntegrationId(null);
    }

    @SuppressWarnings("unchecked")
    private T getNotNullAdapterByIntegrationId(Long integrationId) {
        Optional<IntegrationAdapter> maybeAdapter = getAdapter(integrationId);
        if (integrationId == null) {
            return (T) maybeAdapter.orElseThrow(() -> new IntegrationException(String.format(ERR_MSG_ADAPTER_NOT_FOUND_BY_TYPE, defaultType)));
        } else {
            return (T) maybeAdapter.orElseThrow(() -> new IntegrationException(String.format(ERR_MSG_ADAPTER_NOT_FOUND, integrationId)));
        }
    }

    private Optional<IntegrationAdapter> getAdapter(Long integrationId) {
        Optional<IntegrationAdapter> maybeAdapter;
        if (integrationId == null) {
            // can be null in case of legacy client call - use default adapter
            maybeAdapter = integrationAdapterProxy.getDefaultAdapter(defaultType);
        } else {
            maybeAdapter = IntegrationAdapterProxy.getAdapter(integrationId);
        }
        return maybeAdapter;
    }

    @SuppressWarnings("unchecked")
    public T getAdapterByBackReferenceId(String backReferenceId) {
        return (T) integrationAdapterProxy.getAdapter(backReferenceId)
                                          .orElseThrow(() -> new IntegrationException(String.format(ERR_MSG_ADAPTER_NOT_FOUND_BY_TYPE, defaultType)));
    }

    public IntegrationAdapterProxy getIntegrationAdapterProxy() {
        return integrationAdapterProxy;
    }

    public String getDefaultType() {
        return defaultType;
    }
}
