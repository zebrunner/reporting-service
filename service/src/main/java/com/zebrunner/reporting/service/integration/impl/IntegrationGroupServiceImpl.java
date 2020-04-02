package com.zebrunner.reporting.service.integration.impl;

import com.zebrunner.reporting.domain.entity.integration.IntegrationGroup;
import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import com.zebrunner.reporting.persistence.repository.IntegrationGroupRepository;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IntegrationGroupServiceImpl implements IntegrationGroupService {

    private static final String ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_NAME = "Integration group not found by name: %s";
    private static final String ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_INTEGRATION_TYPE = "Integration group not found by type id: %d";

    private final IntegrationTypeService integrationTypeService;
    private final IntegrationGroupRepository integrationGroupRepository;

    public IntegrationGroupServiceImpl(IntegrationTypeService integrationTypeService,
                                       IntegrationGroupRepository integrationGroupRepository) {
        this.integrationTypeService = integrationTypeService;
        this.integrationGroupRepository = integrationGroupRepository;
    }

    @Override
    @Transactional
    public List<IntegrationGroup> retrieveAll() {
        List<IntegrationType> types = integrationTypeService.retrieveAll();
        List<IntegrationGroup> groups = integrationGroupRepository.findAll();
        groups.forEach(group -> group.setTypes(types.stream().filter(type -> type.getGroup().getId().equals(group.getId())).collect(Collectors.toList())));
        return groups;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationGroup retrieveByIntegrationTypeId(Long integrationTypeId) {
        return integrationGroupRepository.findByTypeId(integrationTypeId)
                                         .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_GROUP_NOT_FUND, ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_INTEGRATION_TYPE, integrationTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationGroup retrieveByName(String name) {
        return integrationGroupRepository.findByName(name)
                                         .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_GROUP_NOT_FUND, ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_NAME, name));
    }

}
