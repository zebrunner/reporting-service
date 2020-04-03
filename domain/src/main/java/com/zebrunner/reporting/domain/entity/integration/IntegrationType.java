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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@NamedEntityGraph(name = "integrationType.expanded", attributeNodes = {
    @NamedAttributeNode("params")
})
@Entity
@Table(name = "integration_types")
public class IntegrationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String iconUrl;

    @OneToMany()
    @JoinColumn(name = "integration_type_id")
    private Set<IntegrationParam> params;

    @OneToMany()
    @JoinColumn(name = "integration_type_id")
    private Set<Integration> integrations;

    @ManyToOne()
    @JoinColumn(name = "integration_group_id")
    private IntegrationGroup group;

}