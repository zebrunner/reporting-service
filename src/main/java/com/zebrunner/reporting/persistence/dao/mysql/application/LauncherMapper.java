package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.launcher.Launcher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LauncherMapper {

    void createLauncher(Launcher launcher);

    void batchCreate(@Param("launchers") List<Launcher> launchers);

    Launcher getLauncherById(Long id);

    Launcher getLauncherByJobId(Long jobId);

    Launcher getByPresetReference(String presetRef);

    List<Launcher> getAllLaunchers(Long userId);

    List<Launcher> getAllAutoScannedByScmAccountId(Long scmAccountId);

    boolean isExistById(Long id);

    void updateLauncher(Launcher launcher);

    void batchUpdate(@Param("launchers") List<Launcher> launchers);

    void deleteLauncherById(Long id);

    void batchDelete(@Param("ids") List<Long> ids);

}
