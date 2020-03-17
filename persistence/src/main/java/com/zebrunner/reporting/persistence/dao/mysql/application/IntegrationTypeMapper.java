package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.integration.IntegrationType;

import java.util.List;

public interface IntegrationTypeMapper {

    IntegrationType findById(Long id);

    IntegrationType findByName(String name);

    IntegrationType findByIntegrationId(Long integrationId);

    List<IntegrationType> findAll();

}
