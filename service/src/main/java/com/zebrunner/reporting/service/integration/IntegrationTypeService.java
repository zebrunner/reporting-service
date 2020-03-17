package com.zebrunner.reporting.service.integration;


import com.zebrunner.reporting.domain.entity.integration.IntegrationType;

import java.util.List;

public interface IntegrationTypeService {

    IntegrationType retrieveById(Long id);

    IntegrationType retrieveByName(String name);

    IntegrationType retrieveByIntegrationId(Long integrationId);

    List<IntegrationType> retrieveAll();
    
}
