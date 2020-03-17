package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.UserPreference;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserPreferenceMapper {
    void createUserPreference(UserPreference userPreference);

    void createUserPreferences(@Param(value = "userId") long userId, @Param(value = "userPreferences") List<UserPreference> userPreferences);

    String getDefaultPreferenceValue(String name);

    UserPreference getUserPreferenceById(long id);

    List<UserPreference> getUserPreferencesByNameAndDashboardTitle(@Param("name") UserPreference.Name name, @Param("title") String title);

    List<UserPreference> getUserPreferencesByUserId(long userId);

    List<UserPreference> getDefaultUserPreferences();

    UserPreference getUserPreferenceByNameAndUserId(@Param("name") String name, @Param("userId") long userId);

    void updateUserPreference(UserPreference userPreference);

    void updateUserPreferences(@Param(value = "userId") long userId, @Param(value = "userPreferences") List<UserPreference> userPreference);

    void deleteUserPreferenceById(long id);

    void deleteUserPreferencesByUserId(long userId);
}
