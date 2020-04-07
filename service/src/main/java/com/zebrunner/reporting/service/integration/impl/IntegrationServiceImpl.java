package com.zebrunner.reporting.service.integration.impl;

import com.zebrunner.reporting.persistence.repository.IntegrationRepository;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.entity.integration.IntegrationGroup;
import com.zebrunner.reporting.domain.entity.integration.IntegrationInfo;
import com.zebrunner.reporting.domain.entity.integration.IntegrationSetting;
import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import com.zebrunner.reporting.domain.push.events.ReinitEventMessage;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.IntegrationSettingService;
import com.zebrunner.reporting.service.integration.IntegrationTypeService;
import com.zebrunner.reporting.service.integration.core.IntegrationInitializer;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.util.EventPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private static final String ERR_MSG_MULTIPLE_INTEGRATIONS_ARE_NOT_ALLOWED = "Multiple integrations of type '%s' are not allowed";
    private static final String ERR_MSG_INTEGRATION_NOT_FOUND_BY_ID = "Integration with id '%d' not found";
    private static final String ERR_MSG_INTEGRATION_NOT_FOUND_BY_BACK_REFERENCE_ID = "Integration with back reference id '%s' not found";
    private static final String ERR_MSG_DEFAULT_VALUE_IS_NOT_PROVIDED_BY_TYPE_ID = "Default value for integration with id '%d' is not provided";
    private static final String ERR_MSG_DEFAULT_VALUE_IS_NOT_PROVIDED_BY_NAME = "Default value for integration with name '%s' is not provided";

    private final IntegrationRepository integrationRepository;
    private final IntegrationGroupService integrationGroupService;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationSettingService integrationSettingService;
    private final EventPushService<ReinitEventMessage> eventPushService;
    private final IntegrationInitializer integrationInitializer;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public IntegrationServiceImpl(
            IntegrationRepository integrationRepository,
            IntegrationGroupService integrationGroupService,
            IntegrationTypeService integrationTypeService,
            IntegrationSettingService integrationSettingService,
            EventPushService<ReinitEventMessage> eventPushService,
            IntegrationInitializer integrationInitializer
    ) {
        this.integrationRepository = integrationRepository;
        this.integrationGroupService = integrationGroupService;
        this.integrationTypeService = integrationTypeService;
        this.integrationSettingService = integrationSettingService;
        this.eventPushService = eventPushService;
        this.integrationInitializer = integrationInitializer;
    }

    @Override
    public Integration create(final Integration integration, Long typeId) {

        Integration createdIntegration = transactionTemplate.execute(paramTransactionStatus -> {
            IntegrationType type = integrationTypeService.retrieveById(typeId);
            verifyMultipleAllowedForType(type);
            unAssignIfDefault(integration, typeId);

            String backReferenceId = generateBackReferenceId(typeId);
            integration.setId(null);
            // TODO: 9/11/19 check with PO if we can persist integration without enabling it / connecting to it
            integration.setEnabled(true);
            integration.setType(type);
            integration.setBackReferenceId(backReferenceId);

            Integration persistedIntegration = integrationRepository.save(integration);

            List<IntegrationSetting> integrationSettings = updateIntegrationSettings(persistedIntegration, typeId);
            persistedIntegration.setSettings(integrationSettings);

            return persistedIntegration;
        });

        notifyToolReInitialized(createdIntegration);

        return createdIntegration;
    }

    private List<IntegrationSetting> updateIntegrationSettings(Integration integration, Long typeId) {
        for (IntegrationSetting setting : integration.getSettings()) {
            setting.setIntegration(integration);
        }
        return integrationSettingService.batchUpdate(integration.getSettings(), typeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveById(Long id) {
        return integrationRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_NOT_FOUND, ERR_MSG_INTEGRATION_NOT_FOUND_BY_ID, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveByBackReferenceId(String backReferenceId) {
        return integrationRepository.findIntegrationByBackReferenceId(backReferenceId)
                                    .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INVITATION_NOT_FOUND, ERR_MSG_INTEGRATION_NOT_FOUND_BY_BACK_REFERENCE_ID, backReferenceId));
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveByJobAndIntegrationTypeName(Job job, String integrationTypeName) {
        List<Integration> integrations = retrieveByIntegrationsTypeName(integrationTypeName);
        String jenkinsHost = job.getJenkinsHost();
        return getIntegrationByJenkinsHost(integrations, jenkinsHost);
    }

    private Integration getIntegrationByJenkinsHost(List<Integration> integrations, String jenkinsHost) {
        return integrations.stream()
                           .filter(integration -> findIntegrationSettingWithJenkinsHost(jenkinsHost, integration))
                           .findAny()
                           .orElse(new Integration());
    }

    private boolean findIntegrationSettingWithJenkinsHost(String jenkinsHost, Integration integration) {
        return integration.getSettings()
                          .stream()
                          .anyMatch(setting -> !StringUtils.isEmpty(setting.getValue()) && setting.getValue().equals(jenkinsHost));
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveDefaultByIntegrationTypeId(Long integrationTypeId) {
        return integrationRepository.findIntegrationByTypeIdAndDefaultIsTrue(integrationTypeId)
                                    .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_NOT_FOUND, ERR_MSG_DEFAULT_VALUE_IS_NOT_PROVIDED_BY_TYPE_ID, integrationTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveIntegrationsByTypeId(Long typeId) {
        return integrationRepository.getIntegrationsByTypeId(typeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveIntegrationsByGroupId(Long groupId) {
        List<Integration> integrations = integrationRepository.findByGroupId(groupId);
        attachTypesAndSettings(integrations);
        return integrations;
    }

    @Override
    @Transactional(readOnly = true)
    public Integration retrieveDefaultByIntegrationTypeName(String integrationTypeName) {
        return integrationRepository.findIntegrationByTypeNameAndDefaultIsTrue(integrationTypeName)
                                    .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_NOT_FOUND, ERR_MSG_DEFAULT_VALUE_IS_NOT_PROVIDED_BY_NAME, integrationTypeName));
    }

    private void attachTypesAndSettings(List<Integration> integrations) {
        integrations.forEach(integration -> {
            IntegrationType type = integrationTypeService.retrieveByIntegrationId(integration.getId());
            integration.setType(type);
            List<IntegrationSetting> settings = integrationSettingService.retrieveByIntegrationId(integration.getId());
            integration.setSettings(settings);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveAll() {
        List<Integration> integrations = integrationRepository.findAll();
        attachTypesAndSettings(integrations);
        return integrations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveIntegrationsByGroupName(String integrationGroupName) {
        List<Integration> integrations = integrationRepository.findIntegrationsByGroupName(integrationGroupName);
        attachTypesAndSettings(integrations);
        return integrations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integration> retrieveByIntegrationsTypeName(String integrationTypeName) {
        return integrationRepository.findIntegrationsByTypeName(integrationTypeName);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Map<String, List<IntegrationInfo>>> retrieveInfo() {
        List<IntegrationGroup> integrationGroups = integrationGroupService.retrieveAll();
        return integrationGroups.stream().map(integrationGroup -> {
            Map<String, List<IntegrationInfo>> integrationInfos = integrationGroup.getTypes().stream().map(integrationType -> {
                List<Integration> integrations = retrieveIntegrationsByTypeId(integrationType.getId());
                List<IntegrationInfo> integrationConnections = buildInfo(integrationGroup.getName(), integrations);
                return new AbstractMap.SimpleEntry<>(integrationType.getName(), integrationConnections);
            }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
            return new AbstractMap.SimpleEntry<>(integrationGroup.getName(), integrationInfos);
        }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationInfo retrieveInfoByIntegrationId(String groupName, Long id) {
        Integration integration = retrieveById(id);
        return collectRuntimeIntegrationInfo(groupName, integration);
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationInfo retrieveInfoByIntegration(Integration integration) {
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByIntegrationTypeId(integration.getType().getId());
        return collectRuntimeIntegrationInfo(integrationGroup.getName(), integration);
    }

    private List<IntegrationInfo> buildInfo(String groupName, List<Integration> integrations) {
        return integrations.stream()
                           .map(integration -> collectRuntimeIntegrationInfo(groupName, integration))
                           .collect(Collectors.toList());
    }

    private IntegrationInfo collectRuntimeIntegrationInfo(String groupName, Integration integration) {
        AbstractIntegrationService integrationService = integrationInitializer.getIntegrationServices().get(groupName);
        boolean enabled = integration.isEnabled();
        boolean connected = false;
        if (enabled) {
            connected = integrationService.isEnabledAndConnected(integration.getId());
        }
        // TODO: 9/11/19 switch connected and enabled places to avoid confusion
        return new IntegrationInfo(integration.getId(), integration.getBackReferenceId(), integration.isDefault(), connected, enabled);
    }

    @Override
    public Integration update(Integration integration) {

        Integration updatedIntegration = transactionTemplate.execute(paramTransactionStatus -> {
            IntegrationType integrationType = integrationTypeService.retrieveByIntegrationId(integration.getId());
            unAssignIfDefault(integration, null);

            Integration dbIntegration = retrieveById(integration.getId());

            // attributes update: update integration attributes and persisted
            integration.setBackReferenceId(dbIntegration.getBackReferenceId());
            integration.setType(dbIntegration.getType());

            List<IntegrationSetting> integrationSettings = updateIntegrationSettings(integration, integrationType.getId());
            integration.setSettings(integrationSettings);

            return integrationRepository.save(integration);
        });

        notifyToolReInitialized(updatedIntegration);

        return updatedIntegration;
    }

    private String generateBackReferenceId(Long integrationTypeId) {
        IntegrationType integrationType = integrationTypeService.retrieveById(integrationTypeId);
        return integrationType.getName() + "_" + UUID.randomUUID().toString();
    }

    private void unAssignIfDefault(Integration integration, Long integrationTypeId) {
        if (integration.isDefault()) {
            if (integrationTypeId == null) { // can be null on update
                IntegrationType integrationType = integrationTypeService.retrieveByIntegrationId(integration.getId());
                integrationTypeId = integrationType.getId();
            }
            Integration defaultIntegration = retrieveDefaultByIntegrationTypeId(integrationTypeId);
            defaultIntegration.setDefault(false);
            integrationRepository.saveAndFlush(defaultIntegration);
        }
    }

    private void verifyMultipleAllowedForType(IntegrationType integrationType) {
        IntegrationGroup integrationGroup = integrationGroupService.retrieveByIntegrationTypeId(integrationType.getId());
        if (!integrationGroup.isMultipleAllowed()) {
            // TODO: 9/11/19 switch to count by type
            List<Integration> integrations = retrieveIntegrationsByTypeId(integrationType.getId());
            if (!integrations.isEmpty()) {
                throw new IllegalOperationException(IllegalOperationException.IllegalOperationErrorDetail.INTEGRATION_CAN_NOT_BE_CREATED, ERR_MSG_MULTIPLE_INTEGRATIONS_ARE_NOT_ALLOWED, integrationType.getName());
            }
        }
    }

    private void notifyToolReInitialized(Integration integration) {
        String tenantName = TenancyContext.getTenantName();
        eventPushService.convertAndSend(EventPushService.Type.SETTINGS, new ReinitEventMessage(tenantName, integration.getId()));
        integrationInitializer.initIntegration(integration, tenantName);
    }

}
