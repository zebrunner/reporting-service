package com.zebrunner.reporting.service.integration.impl;

import com.zebrunner.reporting.persistence.repository.IntegrationSettingRepository;
import com.zebrunner.reporting.domain.entity.integration.IntegrationParam;
import com.zebrunner.reporting.domain.entity.integration.IntegrationSetting;
import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import com.zebrunner.reporting.service.CryptoDriven;
import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.exception.IntegrationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationParamService;
import com.zebrunner.reporting.service.integration.IntegrationSettingService;
import com.zebrunner.reporting.service.integration.IntegrationTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IntegrationSettingServiceImpl implements IntegrationSettingService, CryptoDriven<IntegrationSetting> {

    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND = "Integration setting with id '%d' not found";
    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_ID_AND_PARAM_NAME = "Integration setting with integration id '%d' and parameter name '%s' not found";
    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_TYPE_NAME_AND_PARAM_NAME = "Integration setting with integration type name '%s' and parameter name '%s' not found";
    private static final String ERR_MSG_DUPLICATE_INTEGRATION_SETTINGS = "Duplicate settings for integration type with id %d were found: %s";
    private static final String ERR_MSG_INTEGRATION_SETTINGS_PARAMS_LENGTH = "Integration settings %s are required";
    private static final String ERR_MSG_INTEGRATION_SETTINGS_PARAMS_OWNS = "All settings should to belong to one integration";
    private static final String ERR_MSG_EMPTY_MANDATORY_INTEGRATION_SETTINGS = "Empty mandatory settings for integration type with id %d were found: %s";

    private final IntegrationSettingRepository integrationSettingRepository;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationParamService integrationParamService;
    private final CryptoService cryptoService;

    public IntegrationSettingServiceImpl(IntegrationSettingRepository integrationSettingRepository,
                                         IntegrationTypeService integrationTypeService,
                                         IntegrationParamService integrationParamService,
                                         CryptoService cryptoService
    ) {
        this.integrationSettingRepository = integrationSettingRepository;
        this.integrationTypeService = integrationTypeService;
        this.integrationParamService = integrationParamService;
        this.cryptoService = cryptoService;
    }

    @Override
    @Transactional()
    public List<IntegrationSetting> batchUpdate(List<IntegrationSetting> integrationSettings, Long typeId) {
        validateSettings(integrationSettings, typeId);
        batchEncrypt(integrationSettings);
        Iterable<IntegrationSetting> settingIterable = integrationSettingRepository.saveAll(integrationSettings);
        integrationSettings = new ArrayList<>();
        settingIterable.forEach(integrationSettings::add);
        return integrationSettings;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationSetting retrieveById(Long id) {
        return integrationSettingRepository.findById(id)
                                           .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_SETTING_NOT_FOUND, ERR_MSG_INTEGRATION_SETTING_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationSetting retrieveByIntegrationIdAndParamName(Long integrationId, String paramName) {
        return integrationSettingRepository.findByIntegrationIdAndParamName(integrationId, paramName)
                                           .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_SETTING_NOT_FOUND, ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_ID_AND_PARAM_NAME, integrationId, paramName));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationSetting retrieveByIntegrationTypeNameAndParamName(String integrationTypeName, String paramName) {
        return integrationSettingRepository.findByIntegrationTypeNameAndParamName(integrationTypeName, paramName)
                                           .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_SETTING_NOT_FOUND, ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_TYPE_NAME_AND_PARAM_NAME, integrationTypeName, paramName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationSetting> retrieveAllEncrypted() {
        return integrationSettingRepository.findAllByEncryptedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationSetting> retrieveByIntegrationTypeId(Long integrationTypeId) {
        return integrationSettingRepository.findAllByIntegrationTypeId(integrationTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationSetting> retrieveByIntegrationId(Long integrationId) {
        return integrationSettingRepository.findAllByIntegrationId(integrationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IntegrationSetting update(IntegrationSetting integrationSetting) {
        integrationSettingRepository.save(integrationSetting);
        return integrationSetting;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<IntegrationSetting> getEncryptedCollection() {
        return retrieveAllEncrypted();
    }

    @Override
    public void afterReencryptOperation(Collection<IntegrationSetting> reencryptedCollection) {
        reencryptedCollection.forEach(this::update);
    }

    @Override
    public String getEncryptedValue(IntegrationSetting entity) {
        return entity.getValue();
    }

    @Override
    public void setEncryptedValue(IntegrationSetting integrationSetting, String encryptedString) {
        integrationSetting.setValue(encryptedString);
    }

    private void batchEncrypt(List<IntegrationSetting> integrationSettings) {
        integrationSettings.forEach(this::encryptIfNeed);
    }

    /**
     * Encrypts value on create or update
     * @param integrationSetting - setting to encrypt
     */
    private void encryptIfNeed(IntegrationSetting integrationSetting) {
        String value = integrationSetting.getValue();
        try {
            if (integrationSetting.getId() == null) {
                IntegrationParam integrationParam = integrationParamService.retrieveById(integrationSetting.getParam().getId());
                if (integrationParam.isNeedEncryption()) {
                    integrationSetting.setEncrypted(true);
                    String encryptedValue = cryptoService.encrypt(value);
                    integrationSetting.setValue(encryptedValue);
                }
            } else {
                IntegrationSetting dbIntegrationSetting = retrieveById(integrationSetting.getId());
                if (value == null || value.isBlank()) {
                    integrationSetting.setEncrypted(false);
                } else if (dbIntegrationSetting.getParam().isNeedEncryption() && !value.equals(dbIntegrationSetting.getValue())) {
                    integrationSetting.setEncrypted(true);
                    String encryptedValue = cryptoService.encrypt(value);
                    integrationSetting.setValue(encryptedValue);
                } else {
                    integrationSetting.setEncrypted(dbIntegrationSetting.isEncrypted());
                }
            }
        } catch (ResourceNotFoundException e) {
            integrationSetting.setEncrypted(false);
            IntegrationParam integrationParam = integrationParamService.retrieveById(integrationSetting.getParam().getId());
            if (value == null || value.isBlank()) {
                integrationSetting.setEncrypted(false);
            } else if (integrationParam.isNeedEncryption()) {
                integrationSetting.setEncrypted(true);
                String encryptedValue = cryptoService.encrypt(value);
                integrationSetting.setValue(encryptedValue);
            }
        }
    }

    private void validateSettings(List<IntegrationSetting> integrationSettings, Long integrationTypeId) {
        // check duplicates
        Set<IntegrationSetting> uniqueIntegrationSettings = new HashSet<>(integrationSettings);
        if (integrationSettings.size() != uniqueIntegrationSettings.size()) {
            Set<IntegrationSetting> duplicateSettings = recognizeDuplicateIntegrationSettings(uniqueIntegrationSettings, integrationSettings);
            String duplicates = buildSettingsNameString(duplicateSettings);
            String errorMessage = String.format(ERR_MSG_DUPLICATE_INTEGRATION_SETTINGS, integrationTypeId, duplicates);
            // TODO: 10/15/19 move message to i18n after error message codes logix will be improved
            Map<String, String> additionalErrorInfo = duplicateSettings.stream()
                                                                       .map(integrationSetting -> integrationSetting.getParam().getName())
                                                                       .collect(Collectors.toMap(s -> s, s -> "Duplicate parameter"));
            throw new IntegrationException(errorMessage, additionalErrorInfo);
        }
        // check owns - all parameters for one type and all exist
        List<IntegrationParam> integrationParams = uniqueIntegrationSettings.stream()
                                                                        .map(IntegrationSetting::getParam)
                                                                        .collect(Collectors.toList());


        IntegrationType integrationType = integrationTypeService.retrieveById(integrationTypeId);
        if (integrationType.getParams().size() != integrationParams.size()) {
            String requiredParameters = integrationType.getParams().stream()
                                                       .map(IntegrationParam::getName)
                                                       .collect(Collectors.joining(", "));
            String errorMessage = String.format(ERR_MSG_INTEGRATION_SETTINGS_PARAMS_LENGTH, requiredParameters);
            throw new IntegrationException(errorMessage);
        }
        if (!integrationType.getParams().containsAll(integrationParams)) {
            throw new IntegrationException(ERR_MSG_INTEGRATION_SETTINGS_PARAMS_OWNS);
        }
        // check mandatories
        Set<IntegrationSetting> emptyMandatorySettings = recognizeEmptyMandatoryIntegrationSettings(uniqueIntegrationSettings);
        if (!emptyMandatorySettings.isEmpty()) {
            String emptyMandatories = buildSettingsNameString(emptyMandatorySettings);
            String errorMessage = String.format(ERR_MSG_EMPTY_MANDATORY_INTEGRATION_SETTINGS, integrationTypeId, emptyMandatories);
            Map<String, String> additionalErrorInfo = emptyMandatorySettings.stream()
                                                                            .map(integrationSetting -> integrationSetting.getParam().getName())
                                                                            .collect(Collectors.toMap(s -> s, s -> "Required"));
            throw new IntegrationException(errorMessage, additionalErrorInfo);
        }
    }

    private String buildSettingsNameString(Collection<IntegrationSetting> integrationSettings) {
        return integrationSettings.stream()
                                  .map(integrationSetting -> integrationSetting.getParam().getName())
                                  .collect(Collectors.joining(", "));
    }

    private Set<IntegrationSetting> recognizeDuplicateIntegrationSettings(Set<IntegrationSetting> integrationSettingSet, List<IntegrationSetting> integrationSettings) {
        return integrationSettingSet.stream()
                                    .filter(integrationSetting -> integrationSettings.indexOf(integrationSetting) != integrationSettings.lastIndexOf(integrationSetting))
                                    .collect(Collectors.toSet());
    }

    private Set<IntegrationSetting> recognizeEmptyMandatoryIntegrationSettings(Set<IntegrationSetting> integrationSettingSet) {
        return integrationSettingSet.stream()
                                    .filter(integrationSetting -> {
                                        boolean isValueEmpty = StringUtils.isEmpty(integrationSetting.getValue()) && (integrationSetting.getBinaryData() == null || integrationSetting.getBinaryData().length == 0);
                                        return integrationSetting.getParam().isMandatory() && isValueEmpty;
                                    })
                                    .collect(Collectors.toSet());
    }

}
