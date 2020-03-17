package com.zebrunner.reporting.persistence.repository;

import com.zebrunner.reporting.domain.entity.integration.IntegrationSetting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IntegrationSettingRepository extends CrudRepository<IntegrationSetting, Long> {

    @EntityGraph(value = "integrationSetting.expanded")
    Optional<IntegrationSetting> findById(Long id);

    @EntityGraph(value = "integrationSetting.expanded")
    Optional<IntegrationSetting> findByIntegrationIdAndParamName(Long integrationId, String paramName);

    @EntityGraph(value = "integrationSetting.expanded")
    @Query("select ist from IntegrationSetting ist join ist.param p join ist.integration i join i.type it where it.name = :integrationTypeName and p.name = :paramName and is_default = true")
    Optional<IntegrationSetting> findByIntegrationTypeNameAndParamName(@Param("integrationTypeName") String integrationTypeName, @Param("paramName") String paramName);

    @EntityGraph(value = "integrationSetting.expanded")
    List<IntegrationSetting> findAll();

    @EntityGraph(value = "integrationSetting.expanded")
    @Query("select ist from IntegrationSetting ist join ist.integration i join i.type it where it.id = :integrationTypeId")
    List<IntegrationSetting> findAllByIntegrationTypeId(@Param("integrationTypeId") Long integrationTypeId);

    @EntityGraph(value = "integrationSetting.expanded")
    List<IntegrationSetting> findAllByEncryptedTrue();

}
