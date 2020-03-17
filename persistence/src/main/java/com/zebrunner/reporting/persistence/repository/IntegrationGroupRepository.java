package com.zebrunner.reporting.persistence.repository;

import com.zebrunner.reporting.domain.entity.integration.IntegrationGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IntegrationGroupRepository extends Repository<IntegrationGroup, Long> {

    @EntityGraph(value = "integrationGroup.expanded")
    Optional<IntegrationGroup> findById(Long id);

    @EntityGraph(value = "integrationGroup.expanded")
    Optional<IntegrationGroup> findByName(String name);

    @EntityGraph(value = "integrationGroup.expanded")
    @Query("select ig from IntegrationGroup ig join fetch ig.types it where it.id = :typeId")
    Optional<IntegrationGroup> findByTypeId(@Param("typeId") Long typeId);

    @EntityGraph(value = "integrationGroup.expanded")
    List<IntegrationGroup> findAll();

}
