package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.integration.IntegrationSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface IntegrationSettingMapper {

    void create(@Param("integrationSettings") Set<IntegrationSetting> integrationSettings, @Param("integrationId") Long integrationId);

    IntegrationSetting findById(Long id);

    IntegrationSetting findByIntegrationIdAndParamName(@Param("integrationId") Long integrationId, @Param("paramName") String paramName);

    IntegrationSetting findByIntegrationTypeNameAndParamName(@Param("integrationTypeName") String integrationTypeName, @Param("paramName") String paramName);

    List<IntegrationSetting> findAll();

    List<IntegrationSetting> findAllEncrypted();

    void update(IntegrationSetting integrationSetting);

}
