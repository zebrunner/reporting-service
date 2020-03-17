package com.zebrunner.reporting.persistence.repository;

import com.zebrunner.reporting.domain.entity.integration.IntegrationParam;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface IntegrationParamRepository extends Repository<IntegrationParam, Long> {

    Optional<IntegrationParam> findById(Long id);

    List<IntegrationParam> findAll();


}
