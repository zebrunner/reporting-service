package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.UserPreferenceMapper;
import com.zebrunner.reporting.domain.db.UserPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserPreferenceService {

    private static final String DEFAULT_DASHBOARD_NAME = "General";

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Transactional(rollbackFor = Exception.class)
    public void createDefaultUserPreferences(long userId) {
        userPreferenceMapper.createUserPreferences(userId, getDefaultUserPreferences());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createUserPreference(UserPreference userPreference) {
        userPreferenceMapper.createUserPreference(userPreference);
    }

    @Transactional(readOnly = true)
    public List<UserPreference> getAllUserPreferences(Long userId) {
        return userPreferenceMapper.getUserPreferencesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<UserPreference> getDefaultUserPreferences() {
        return userPreferenceMapper.getDefaultUserPreferences();
    }

    @Transactional(readOnly = true)
    public List<UserPreference> getUserPreferencesByNameAndDashboardTitle(UserPreference.Name name, String title) {
        return userPreferenceMapper.getUserPreferencesByNameAndDashboardTitle(name, title);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserPreference updateUserPreference(UserPreference userPreference) {
        userPreferenceMapper.updateUserPreference(userPreference);
        return userPreference;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UserPreference> updateUserPreferences(long userId, List<UserPreference> userPreferences) {
        userPreferenceMapper.updateUserPreferences(userId, userPreferences);
        return userPreferences;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUserPreferencesByUserId(Long userId) {
        userPreferenceMapper.deleteUserPreferencesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserPreference getUserPreferenceByNameAndUserId(String name, long userId) {
        return userPreferenceMapper.getUserPreferenceByNameAndUserId(name, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UserPreference> resetUserPreferencesToDefault(long userId) {
        return updateUserPreferences(userId, getDefaultUserPreferences());
    }

    @Transactional(rollbackFor = Exception.class)
    public UserPreference createOrUpdateUserPreference(UserPreference newUserPreference) {
        UserPreference userPreference = getUserPreferenceByNameAndUserId(newUserPreference.getName().name(), newUserPreference.getUserId());
        if (userPreference == null) {
            createUserPreference(newUserPreference);
        } else if (!userPreference.equals(newUserPreference)) {
            newUserPreference.setId(userPreference.getId());
            updateUserPreference(newUserPreference);
        } else {
            newUserPreference = userPreference;
        }
        return newUserPreference;
    }

    public void resetDefaultDashboardPreference(String fromTitle) {
        updateDefaultDashboardPreference(fromTitle, DEFAULT_DASHBOARD_NAME);
    }

    @Transactional
    public void updateDefaultDashboardPreference(String fromTitle, String toTitle) {
        List<UserPreference> userPreferences = getUserPreferencesByNameAndDashboardTitle(UserPreference.Name.DEFAULT_DASHBOARD, fromTitle);
        for (UserPreference userPreference : userPreferences) {
            userPreference.setValue(toTitle);
            updateUserPreference(userPreference);
        }
    }
}
