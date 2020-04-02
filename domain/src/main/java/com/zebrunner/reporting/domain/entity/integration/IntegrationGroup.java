package com.zebrunner.reporting.domain.entity.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@NamedEntityGraph(
        name = "integrationGroup.expanded",
        attributeNodes = {
                @NamedAttributeNode(value = "types", subgraph = "types-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "types-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("params")
                        }
                )
        }
)
@Table(name = "integration_groups")
public class IntegrationGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String iconUrl;
    private String displayName;
    private boolean multipleAllowed;

    @OneToMany(mappedBy = "group")
    private List<IntegrationType> types;

}
