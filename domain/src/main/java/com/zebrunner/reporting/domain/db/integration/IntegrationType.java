package com.zebrunner.reporting.domain.db.integration;

import com.zebrunner.reporting.domain.db.AbstractEntity;

import java.util.List;

public class IntegrationType extends AbstractEntity {

    private String name;
    private String displayName;
    private String iconUrl;
    private List<Integration> integrations;
    private List<IntegrationParam> integrationParams;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public List<Integration> getIntegrations() {
        return integrations;
    }

    public void setIntegrations(List<Integration> integrations) {
        this.integrations = integrations;
    }

    public List<IntegrationParam> getIntegrationParams() {
        return integrationParams;
    }

    public void setIntegrationParams(List<IntegrationParam> integrationParams) {
        this.integrationParams = integrationParams;
    }

}
