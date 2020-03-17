package com.zebrunner.reporting.domain.db.integration;

import com.zebrunner.reporting.domain.db.AbstractEntity;

import java.util.List;

public class IntegrationGroup extends AbstractEntity {

    private String name;
    private String iconUrl;
    private boolean multipleAllowed;
    private List<IntegrationType> integrationTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isMultipleAllowed() {
        return multipleAllowed;
    }

    public void setMultipleAllowed(boolean multipleAllowed) {
        this.multipleAllowed = multipleAllowed;
    }

    public List<IntegrationType> getIntegrationTypes() {
        return integrationTypes;
    }

    public void setIntegrationTypes(List<IntegrationType> integrationTypes) {
        this.integrationTypes = integrationTypes;
    }

}
