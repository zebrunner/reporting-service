package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LauncherPreset extends AbstractEntity {

    private String name;
    private String ref;
    private String params;
}
