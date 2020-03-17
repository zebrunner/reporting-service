package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.LauncherCallback;

public interface LauncherCallbackMapper {

    void create(LauncherCallback callback);

    LauncherCallback findByCiRunId(String ciRunId);

    LauncherCallback findByRef(String ref);

    boolean existsByCiRunId(String cirunId);

    void deleteById(Long id);
}
