package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.LauncherPresetMapper;
import com.zebrunner.reporting.domain.db.launcher.LauncherPreset;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;
import com.zebrunner.reporting.service.integration.tool.impl.TestAutomationToolService;
import com.zebrunner.reporting.service.util.URLResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.LAUNCHER_PRESET_CAN_NOT_BE_CREATED;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.LAUNCHER_PRESET_NOT_FOUND;

@Service
public class LauncherPresetService {

    private static final String ERR_MSG_PRESET_IN_USE = "Launcher preset with name '%s' for launcher with id '%d' already in use";
    private static final String ERR_MSG_LAUNCHER_PRESET_NOT_FOUND_BY_ID = "Launcher preset not found by id '%d'";
    private static final String ERR_MSG_LAUNCHER_PRESET_NOT_FOUND_BY_REF = "Launcher preset not found by ref '%s'";

    private static final String WEBHOOK_API_URL_PATTERN = "%s/api/launchers/hooks/%s";

    private final LauncherPresetMapper launcherPresetMapper;
    private final URLResolver urlResolver;
    private final TestAutomationToolService testAutomationToolService;

    public LauncherPresetService(LauncherPresetMapper launcherPresetMapper, URLResolver urlResolver, TestAutomationToolService testAutomationToolService) {
        this.launcherPresetMapper = launcherPresetMapper;
        this.urlResolver = urlResolver;
        this.testAutomationToolService = testAutomationToolService;
    }

    @Transactional()
    public LauncherPreset create(LauncherPreset launcherPreset, Long launcherId) {
        launcherPreset.setId(null);
        if (!canPersist(launcherPreset, launcherId)) {
            throw new IllegalOperationException(LAUNCHER_PRESET_CAN_NOT_BE_CREATED, String.format(ERR_MSG_PRESET_IN_USE, launcherPreset.getName(), launcherId));
        }

        String ref = generateRef();
        launcherPreset.setRef(ref);
        launcherPresetMapper.create(launcherPreset, launcherId);
        return launcherPreset;
    }

    @Transactional(readOnly = true)
    public LauncherPreset retrieveById(Long id) {
        LauncherPreset launcherPreset = launcherPresetMapper.findById(id);
        if (launcherPreset == null) {
            throw new ResourceNotFoundException(LAUNCHER_PRESET_NOT_FOUND, String.format(ERR_MSG_LAUNCHER_PRESET_NOT_FOUND_BY_ID, id));
        }
        return launcherPreset;
    }

    @Transactional(readOnly = true)
    public LauncherPreset retrieveByIdAndReference(Long id, String ref) {
        LauncherPreset launcherPreset = launcherPresetMapper.findByIdAndRef(id, ref);
        if (launcherPreset == null) {
            throw new ResourceNotFoundException(LAUNCHER_PRESET_NOT_FOUND, String.format(ERR_MSG_LAUNCHER_PRESET_NOT_FOUND_BY_ID, id));
        }
        return launcherPreset;
    }

    @Transactional(readOnly = true)
    public LauncherPreset retrieveByRef(String ref) {
        LauncherPreset launcherPreset = launcherPresetMapper.findByRef(ref);
        if (launcherPreset == null) {
            throw new ResourceNotFoundException(LAUNCHER_PRESET_NOT_FOUND, String.format(ERR_MSG_LAUNCHER_PRESET_NOT_FOUND_BY_REF, ref));
        }
        return launcherPreset;
    }

    @Transactional(readOnly = true)
    public boolean existsByNameAndLauncherId(String name, Long launcherId) {
        return launcherPresetMapper.existsByNameAndLauncherId(name, launcherId);
    }

    @Transactional
    public LauncherPreset update(LauncherPreset launcherPreset, Long launcherId) {
        LauncherPreset dbLauncherPreset = retrieveById(launcherPreset.getId());
        boolean persistDenied = !dbLauncherPreset.getName().equals(launcherPreset.getName()) && !canPersist(launcherPreset, launcherId);
        if (persistDenied) {
            throw new IllegalOperationException(LAUNCHER_PRESET_CAN_NOT_BE_CREATED, String.format(ERR_MSG_PRESET_IN_USE, launcherPreset.getName(), launcherId));
        }

        launcherPreset.setRef(dbLauncherPreset.getRef());
        launcherPresetMapper.update(launcherPreset);
        return launcherPreset;
    }

    @Transactional
    public void deleteByIdAndLauncherId(Long id, Long launcherId) {
        launcherPresetMapper.deleteByIdAndLauncherId(id, launcherId);
    }

    @Transactional
    public void updateReference(Long id, String ref) {
        launcherPresetMapper.updateReference(id, ref);
    }

    @Transactional(readOnly = true)
    public String buildWebHookUrl(Long id) {
        LauncherPreset launcherPreset = retrieveById(id);
        String webserviceUrl = urlResolver.buildWebserviceUrl();
        return String.format(WEBHOOK_API_URL_PATTERN, webserviceUrl, launcherPreset.getRef());
    }

    @Transactional
    public void revokeReference(Long id, String oldRef, Long launcherId) {
        LauncherPreset dbLauncherPreset = retrieveByIdAndReference(id, oldRef);
        boolean presetNotExists = canPersist(dbLauncherPreset, launcherId);
        if (presetNotExists) {
            throw new IllegalOperationException(LAUNCHER_PRESET_CAN_NOT_BE_CREATED, String.format(ERR_MSG_PRESET_IN_USE, dbLauncherPreset.getName(), launcherId));
        }

        String newRef = generateRef();
        updateReference(id, newRef);
    }

    public Long getTestEnvironmentProviderId(Long id) {
        IntegrationGroupAdapter groupAdapter = testAutomationToolService.getAdapterByIntegrationId(id);
        return groupAdapter.getIntegrationId();
    }

    private boolean canPersist(LauncherPreset launcherPreset, Long launcherId) {
        boolean duplicateName = existsByNameAndLauncherId(launcherPreset.getName(), launcherId);
        return !duplicateName;
    }

    private String generateRef() {
        return RandomStringUtils.randomAlphabetic(20);
    }
}
