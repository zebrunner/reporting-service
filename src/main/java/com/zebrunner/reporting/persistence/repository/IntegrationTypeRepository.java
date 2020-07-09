package com.zebrunner.reporting.persistence.repository;

import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IntegrationTypeRepository extends Repository<IntegrationType, Long> {

    @EntityGraph(value = "integrationType.expanded")
    Optional<IntegrationType> findById(Long id);

    @EntityGraph(value = "integrationType.expanded")
    Optional<IntegrationType> findByName(String name);

    @EntityGraph(value = "integrationType.expanded")
    @Query(value = "select it from IntegrationType it join it.integrations i where i.id = :integrationId")
    Optional<IntegrationType> findByIntegrationId(@Param("integrationId") Long integrationId);

    @EntityGraph(value = "integrationType.expanded")
    List<IntegrationType> findAll();

}
