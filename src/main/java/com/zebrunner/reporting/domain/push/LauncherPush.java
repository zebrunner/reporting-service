package com.zebrunner.reporting.domain.push;

import com.zebrunner.reporting.domain.dto.LauncherDTO;
import lombok.Getter;

import java.util.List;

import static com.zebrunner.reporting.domain.push.AbstractPush.Type.LAUNCHER;

@Getter
public class LauncherPush extends AbstractPush {

    private final List<LauncherDTO> launchers;
    private final Long userId;
    private final boolean success;

    public LauncherPush(List<LauncherDTO> launchers, Long userId, boolean success) {
        super(LAUNCHER);
        this.launchers = launchers;
        this.userId = userId;
        this.success = success;
    }

}
