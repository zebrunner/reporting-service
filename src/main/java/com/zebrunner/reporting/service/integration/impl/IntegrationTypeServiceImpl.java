package com.zebrunner.reporting.service.integration.impl;

import com.zebrunner.reporting.persistence.repository.IntegrationTypeRepository;
import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntegrationTypeServiceImpl implements IntegrationTypeService {

    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND = "No integration types found by id: %d";
    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_NAME = "No integration types found by name: %s";
    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_INTEGRATION_ID = "No integration types found by integration id: %d";

    private final IntegrationTypeRepository integrationTypeRepository;

    public IntegrationTypeServiceImpl(IntegrationTypeRepository integrationTypeRepository) {
        this.integrationTypeRepository = integrationTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationType retrieveById(Long id) {
        return integrationTypeRepository.findById(id)
                                        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_TYPE_NOT_FOUND, ERR_MSG_INTEGRATION_TYPE_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationType retrieveByName(String name) {
        return integrationTypeRepository.findByName(name)
                                        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_TYPE_NOT_FOUND, ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_NAME, name));
    }

    @Override
    public IntegrationType retrieveByIntegrationId(Long integrationId) {
        return integrationTypeRepository.findByIntegrationId(integrationId)
                                        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_TYPE_NOT_FOUND, ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_INTEGRATION_ID, integrationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationType> retrieveAll() {
        return integrationTypeRepository.findAll();
    }

}
