package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.integration.Integration;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IntegrationMapper {

    void create(@Param("integration") Integration integration, @Param("integrationTypeId") Long integrationTypeId);

    Integration findById(Long id);

    Integration findByBackReferenceId(String backReferenceId);

    Integration findDefaultByIntegrationTypeId(Long integrationTypeId);

    Integration findDefaultByIntegrationTypeName(String integrationTypeName);

    List<Integration> findAll();

    List<Integration> findByIntegrationTypeId(Long integrationTypeId);

    List<Integration> findByIntegrationGroupName(String integrationGroupName);

    void update(Integration integration);

}
