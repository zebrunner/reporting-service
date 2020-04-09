package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.launcher.UserLauncherPreference;
import org.apache.ibatis.annotations.Param;

public interface UserLauncherPreferenceMapper {

    void create(@Param("launcherId") Long launcherId, @Param("userId") Long userId, @Param("userLauncherPreference") UserLauncherPreference userLauncherPreference);

    UserLauncherPreference findByLauncherIdAndUserId(@Param("launcherId") Long launcherId, @Param("userId") Long userId);

    boolean isExistByLauncherIdAndUserId(@Param("launcherId") Long launcherId, @Param("userId") Long userId);

    void update(UserLauncherPreference preference);
}
