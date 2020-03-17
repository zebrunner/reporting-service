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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@NamedEntityGraph(name = "integrationGroup.expanded", attributeNodes = {
    @NamedAttributeNode("types")
})
@Table(name = "integration_groups")
public class IntegrationGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String iconUrl;
    private String displayName;
    private boolean multipleAllowed;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
    private List<IntegrationType> types;

}
