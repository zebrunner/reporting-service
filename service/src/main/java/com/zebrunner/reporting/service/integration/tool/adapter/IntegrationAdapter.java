package com.zebrunner.reporting.service.integration.tool.adapter;

/**
 * Represents adapter for specific integration instance of certain type.
 * Adapter is created per integration rather than per integration type or group.
 */
public interface IntegrationAdapter extends IntegrationGroupAdapter {

    /**
     * Returns current adapter connectivity state.
     * @return {@code true} if adapter is connected
     */
    boolean isConnected();

}
