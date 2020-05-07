package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.launcher.LauncherArtifact;
import com.zebrunner.reporting.persistence.dao.mysql.application.LauncherArtifactMapper;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.LAUNCHER_ARTIFACT_CAN_NOT_BE_CREATED;

@Service
public class LauncherArtifactService {

    private static final String ERR_MSG_LAUNCHER_ARTIFACT_ALREADY_EXISTS = "Artifact for launcher with id %d and name %s already exists";

    private final LauncherArtifactMapper launcherArtifactMapper;

    public LauncherArtifactService(LauncherArtifactMapper launcherArtifactMapper) {
        this.launcherArtifactMapper = launcherArtifactMapper;
    }

    @Transactional
    public LauncherArtifact create(LauncherArtifact artifact, Long launcherId) {
        boolean existsByNameAndLauncherId = launcherArtifactMapper.existsByNameAndLauncherId(artifact.getName(), launcherId);
        if (existsByNameAndLauncherId) {
            throw new IllegalOperationException(LAUNCHER_ARTIFACT_CAN_NOT_BE_CREATED, String.format(ERR_MSG_LAUNCHER_ARTIFACT_ALREADY_EXISTS, launcherId, artifact.getName()));
        }
        launcherArtifactMapper.create(artifact, launcherId);
        return artifact;
    }
}
