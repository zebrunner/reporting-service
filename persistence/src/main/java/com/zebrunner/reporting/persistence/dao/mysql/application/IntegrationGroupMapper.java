package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.integration.IntegrationGroup;

import java.util.List;

public interface IntegrationGroupMapper {

    IntegrationGroup findById(Long id);

    IntegrationGroup findByName(String name);

    IntegrationGroup findByIntegrationTypeId(Long integrationTypeId);

    List<IntegrationGroup> findAll();

}