package com.zebrunner.reporting.service.integration;


import com.zebrunner.reporting.domain.entity.integration.IntegrationSetting;

import java.util.List;

public interface IntegrationSettingService {

    List<IntegrationSetting> batchUpdate(List<IntegrationSetting> integrationSettings, Long typeId);

    IntegrationSetting retrieveById(Long id);

    IntegrationSetting retrieveByIntegrationIdAndParamName(Long integrationId, String paramName);

    IntegrationSetting retrieveByIntegrationTypeNameAndParamName(String integrationTypeName, String paramName);

    List<IntegrationSetting> retrieveAllEncrypted();

    List<IntegrationSetting> retrieveByIntegrationTypeId(Long integrationTypeId);

    IntegrationSetting update(IntegrationSetting integrationSetting);

}
