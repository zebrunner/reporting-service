package com.zebrunner.reporting.domain.db.launcher;

import com.zebrunner.reporting.domain.db.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LauncherPreset extends AbstractEntity {

    private String name;
    private String ref;
    private String params;
}
