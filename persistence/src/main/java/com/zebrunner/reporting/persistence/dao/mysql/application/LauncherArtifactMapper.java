package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.launcher.LauncherArtifact;
import org.apache.ibatis.annotations.Param;

public interface LauncherArtifactMapper {

    void create(@Param("artifact") LauncherArtifact artifact, @Param("launcherId") Long launcherId);

    boolean existsByNameAndLauncherId(@Param("name") String name, @Param("launcherId") Long launcherId);

}
