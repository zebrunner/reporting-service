package com.zebrunner.reporting.persistence.repository;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IntegrationRepository extends JpaRepository<Integration, Long> {

    @EntityGraph(value = "integration.expanded")
    Optional<Integration> findById(Long name);

    @EntityGraph(value = "integration.expanded")
    List<Integration> findAll();

    @EntityGraph(value = "integration.expanded")
    List<Integration> getIntegrationsByTypeId(Long typeId);

    @EntityGraph(value = "integration.expanded")
    @Query("select i from Integration i join fetch i.settings s join fetch s.param join fetch i.type t join t.group g where g.id = :groupId")
    List<Integration> findByGroupId(@Param("groupId") Long groupId);

    @EntityGraph(value = "integration.expanded")
    Optional<Integration> findIntegrationByBackReferenceId(String backReferenceId);

    @EntityGraph(value = "integration.expanded")
    @Query("select i from Integration i join fetch i.settings s join fetch s.param join fetch i.type t join t.group g where g.name = :groupName")
    List<Integration> findIntegrationsByGroupName(@Param("groupName") String groupName);

    @EntityGraph(value = "integration.expanded")
    @Query("Select i From Integration i Where i.type.id = :integrationTypeId and i.isDefault = true")
    Optional<Integration> findIntegrationByTypeIdAndDefaultIsTrue(@Param("integrationTypeId") Long integrationTypeId);

    @EntityGraph(value = "integration.expanded")
    @Query("Select i From Integration i Where i.type.name = :integrationTypeName and i.isDefault = true")
    Optional<Integration> findIntegrationByTypeNameAndDefaultIsTrue(@Param("integrationTypeName") String integrationTypeName);

    @EntityGraph(value = "integration.expanded")
    List<Integration> findIntegrationsByTypeName(String integrationTypeName);

}
