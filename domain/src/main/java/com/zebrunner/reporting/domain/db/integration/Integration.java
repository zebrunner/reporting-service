package com.zebrunner.reporting.domain.db.integration;

import com.zebrunner.reporting.domain.db.AbstractEntity;

import java.util.List;
import java.util.Optional;

public class Integration extends AbstractEntity {

    private String name;
    private String backReferenceId;
    private boolean isDefault;
    private boolean enabled;
    private List<IntegrationSetting> integrationSettings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackReferenceId() {
        return backReferenceId;
    }

    public void setBackReferenceId(String backReferenceId) {
        this.backReferenceId = backReferenceId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<IntegrationSetting> getIntegrationSettings() {
        return integrationSettings;
    }

    public void setIntegrationSettings(List<IntegrationSetting> integrationSettings) {
        this.integrationSettings = integrationSettings;
    }

    public Optional<IntegrationSetting> getAttribute(String attributeName) {
        return this.getIntegrationSettings().stream()
                   .filter(is -> is.getIntegrationParam().getName().equals(attributeName))
                   .findAny();
    }

    public Optional<String> getAttributeValue(String attributeName) {
        IntegrationSetting integrationSetting = getAttribute(attributeName)
                .orElse(null);
        String value = integrationSetting == null ? null : integrationSetting.getValue();
        return Optional.ofNullable(value);
    }

    public Optional<byte[]> getAttributeBinaryData(String attributeName) {
        IntegrationSetting integrationSetting = getAttribute(attributeName)
                .orElse(null);
        byte[] binaryData = integrationSetting == null ? null : integrationSetting.getBinaryData();
        return Optional.ofNullable(binaryData);
    }

}
