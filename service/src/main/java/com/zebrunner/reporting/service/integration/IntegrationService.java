package com.zebrunner.reporting.service.integration;

import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.entity.integration.IntegrationInfo;
import com.zebrunner.reporting.domain.entity.integration.IntegrationPublicInfo;

import java.util.List;
import java.util.Map;

public interface IntegrationService {

    Integration create(Integration integration, Long integrationTypeId);

    Integration retrieveById(Long id);

    Integration retrieveByBackReferenceId(String backReferenceId);

    Integration retrieveByJobAndIntegrationTypeName(Job job, String integrationTypeName);

    Integration retrieveDefaultByIntegrationTypeId(Long integrationTypeId);

    Integration retrieveDefaultByIntegrationTypeName(String integrationTypeName);

    List<Integration> retrieveAll();

    List<Integration> retrieveIntegrationsByTypeId(Long typeId);

    List<Integration> retrieveIntegrationsByGroupId(Long groupId);

    List<Integration> retrieveIntegrationsByGroupName(String integrationGroupName);

    List<Integration> retrieveByIntegrationsTypeName(String integrationTypeName);

    List<IntegrationPublicInfo> retrievePublicInfo();

    Map<String, Map<String, List<IntegrationInfo>>> retrieveInfo();

    IntegrationInfo retrieveInfoByIntegrationId(String groupName, Long id);

    IntegrationInfo retrieveInfoByIntegration(Integration integration);

    Integration update(Integration integration);

}
