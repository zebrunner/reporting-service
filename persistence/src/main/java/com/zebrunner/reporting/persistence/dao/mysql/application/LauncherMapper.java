package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.launcher.Launcher;

import java.util.List;

public interface LauncherMapper {

    void createLauncher(Launcher launcher);

    Launcher getLauncherById(Long id);

    Launcher getLauncherByJobId(Long jobId);

    Launcher getByPresetReference(String presetRef);

    List<Launcher> getAllLaunchers(Long userId);

    boolean isExistById(Long id);

    void updateLauncher(Launcher launcher);

    void deleteLauncherById(Long id);

    void deleteAutoScannedLaunchersByScmAccountId(Long scmAccountId);

}
