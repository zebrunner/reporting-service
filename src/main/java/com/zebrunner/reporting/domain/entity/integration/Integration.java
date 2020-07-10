package com.zebrunner.reporting.domain.entity.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@NamedEntityGraph(
        name = "integration.expanded",
        attributeNodes = {
                @NamedAttributeNode("type"),
                @NamedAttributeNode(value = "settings", subgraph = "settings-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "settings-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("param")
                        }
                )
        }
)
@Entity
@Table(name = "integrations")
public class Integration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String backReferenceId;
    private boolean isDefault;
    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "integration_type_id")
    private IntegrationType type;

    @OneToMany(mappedBy = "integration")
    private List<IntegrationSetting> settings;

    public String getAttributeValue(String attributeName) {
        IntegrationSetting integrationSetting = getAttribute(attributeName)
                .orElse(null);
        return integrationSetting == null ? null : integrationSetting.getValue();
    }

    public byte[] getAttributeBinaryData(String attributeName) {
        IntegrationSetting integrationSetting = getAttribute(attributeName)
                .orElse(null);
        return integrationSetting == null ? null : integrationSetting.getBinaryData();
    }

    private Optional<IntegrationSetting> getAttribute(String attributeName) {
        return this.getSettings().stream()
                   .filter(is -> is.getParam().getName().equalsIgnoreCase(attributeName))
                   .findAny();
    }

    @Override
    public String toString() {
        return name + ": " + enabled;
    }
}
