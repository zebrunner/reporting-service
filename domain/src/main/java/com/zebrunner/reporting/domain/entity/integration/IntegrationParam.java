package com.zebrunner.reporting.domain.entity.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "integration_params")
public class IntegrationParam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String metadata;
    private String defaultValue;
    private boolean mandatory;
    private boolean needEncryption;

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
