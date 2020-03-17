package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.integration.IntegrationParam;

import java.util.List;

public interface IntegrationParamMapper {

    IntegrationParam findById(Long id);

    List<IntegrationParam> findAll();

}
