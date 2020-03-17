package com.zebrunner.reporting.service.integration;

import com.zebrunner.reporting.domain.entity.integration.IntegrationGroup;

import java.util.List;

public interface IntegrationGroupService {

    List<IntegrationGroup> retrieveAll();

    IntegrationGroup retrieveByIntegrationTypeId(Long integrationTypeId);

    IntegrationGroup retrieveByName(String name);

}
