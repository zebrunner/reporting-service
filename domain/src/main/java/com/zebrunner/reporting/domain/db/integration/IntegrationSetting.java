package com.zebrunner.reporting.domain.db.integration;

import com.zebrunner.reporting.domain.db.AbstractEntity;

public class IntegrationSetting extends AbstractEntity {

    private String value;
    private byte[] binaryData;
    private boolean encrypted;
    private IntegrationParam integrationParam;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public IntegrationParam getIntegrationParam() {
        return integrationParam;
    }

    public void setIntegrationParam(IntegrationParam integrationParam) {
        this.integrationParam = integrationParam;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof IntegrationSetting) {
            IntegrationSetting integrationSetting = (IntegrationSetting) obj;
            if (integrationSetting.getId() != null && getId() != null) {
                equals = hashCode() == integrationSetting.hashCode();
            } else if (integrationParam != null && integrationSetting.getIntegrationParam() != null) {
                equals = integrationParam.equals(integrationSetting.getIntegrationParam());
            }
        }
        return equals;
    }

}
