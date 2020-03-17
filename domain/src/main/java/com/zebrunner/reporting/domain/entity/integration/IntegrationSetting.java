package com.zebrunner.reporting.domain.entity.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@NamedEntityGraph(name = "integrationSetting.expanded", attributeNodes = {
        @NamedAttributeNode("param")
})
@Table(name = "integration_settings")
public class IntegrationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    private byte[] binaryData;
    private boolean encrypted;

    @OneToOne
    @JoinColumn(name = "integration_param_id")
    private IntegrationParam param;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="integration_id")
    private Integration integration;

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof IntegrationSetting) {
            IntegrationSetting integrationSetting = (IntegrationSetting) obj;
            if (integrationSetting.getId() != null && getId() != null && integrationSetting.getId() != 0 && getId() != 0) {
                equals = hashCode() == integrationSetting.hashCode();
            } else if (param != null && integrationSetting.getParam() != null) {
                equals = param.equals(integrationSetting.getParam());
            }
        }
        return equals;
    }

}
