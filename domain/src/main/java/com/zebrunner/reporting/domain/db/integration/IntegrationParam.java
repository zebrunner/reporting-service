package com.zebrunner.reporting.domain.db.integration;

import com.zebrunner.reporting.domain.db.AbstractEntity;

public class IntegrationParam extends AbstractEntity {

    private String name;
    private String metadata;
    private String defaultValue;
    private boolean mandatory;
    private boolean needEncryption;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isNeedEncryption() {
        return needEncryption;
    }

    public void setNeedEncryption(boolean needEncryption) {
        this.needEncryption = needEncryption;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof IntegrationParam) {
            IntegrationParam integrationParam = (IntegrationParam) obj;
            if (getId() != null && integrationParam.getId() != null) {
                equals = hashCode() == integrationParam.hashCode();
            }
        }
        return equals;
    }

}
