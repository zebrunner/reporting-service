package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.SettingsMapper;
import com.zebrunner.reporting.domain.db.Setting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {

    private final SettingsMapper settingsMapper;

    public SettingsService(SettingsMapper settingsMapper) {
        this.settingsMapper = settingsMapper;
    }

    @Transactional(readOnly = true)
    public Setting getSettingByName(String name) {
        return settingsMapper.getSettingByName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    public Setting updateSetting(Setting setting) {
        //setting.setValue(StringUtils.isBlank(setting.getValue() != null ? setting.getValue().trim() : null) ? null : setting.getValue());
        settingsMapper.updateSetting(setting);
        return setting;
    }

    @Transactional(readOnly = true)
    public String getPostgresVersion() {
        return settingsMapper.getPostgresVersion();
    }

}
