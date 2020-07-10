package com.zebrunner.reporting.domain.push;

import com.zebrunner.reporting.domain.db.launcher.Launcher;
import lombok.Getter;

@Getter
public class LauncherRunPush extends AbstractPush {

    private final Launcher launcher;
    private final String ciRunId;

    public LauncherRunPush(Launcher launcher, String ciRunId) {
        super(Type.LAUNCHER_RUN);
        launcher.setJob(null);
        launcher.setScmAccount(null);
        this.launcher = launcher;
        this.ciRunId = ciRunId;
    }

}
