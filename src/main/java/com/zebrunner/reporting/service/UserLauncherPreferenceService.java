package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.launcher.UserLauncherPreference;
import com.zebrunner.reporting.persistence.dao.mysql.application.UserLauncherPreferenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserLauncherPreferenceService {

    private final UserLauncherPreferenceMapper preferenceMapper;

    public UserLauncherPreferenceService(UserLauncherPreferenceMapper preferenceMapper) {
        this.preferenceMapper = preferenceMapper;
    }

    @Transactional
    public UserLauncherPreference create(Long launcherId, Long userId, UserLauncherPreference preference) {
        preferenceMapper.create(launcherId, userId, preference);
        return preference;
    }

    @Transactional(readOnly = true)
    public UserLauncherPreference retrieveByLauncherIdAndUserId(Long launcherId, Long userId) {
        return preferenceMapper.findByLauncherIdAndUserId(launcherId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isExistByLauncherIdAndUserId(Long launcherId, Long userId) {
        return preferenceMapper.isExistByLauncherIdAndUserId(launcherId, userId);
    }

    @Transactional
    public UserLauncherPreference update(UserLauncherPreference preference) {
        preferenceMapper.update(preference);
        return preference;
    }
}
