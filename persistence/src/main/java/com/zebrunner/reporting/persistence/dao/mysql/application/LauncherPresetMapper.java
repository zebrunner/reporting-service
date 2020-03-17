package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.LauncherPreset;
import org.apache.ibatis.annotations.Param;

public interface LauncherPresetMapper {

    void create(@Param("preset") LauncherPreset launcherPreset, @Param("launcherId") Long launcherId);

    LauncherPreset findById(Long id);

    LauncherPreset findByRef(String ref);

    boolean existsByNameAndLauncherId(@Param("name") String name, @Param("launcherId") Long launcherId);

    void update(LauncherPreset launcherPreset);

    void deleteById(Long id);

}
