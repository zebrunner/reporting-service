package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.Launcher;

import java.util.List;

public interface LauncherMapper {

    void createLauncher(Launcher launcher);

    Launcher getLauncherById(Long id);

    Launcher getLauncherByJobId(Long jobId);

    Launcher getByPresetReference(String presetRef);

    List<Launcher> getAllLaunchers();

    void updateLauncher(Launcher launcher);

    void deleteLauncherById(Long id);

    void deleteAutoScannedLaunchersByScmAccountId(Long scmAccountId);

}
