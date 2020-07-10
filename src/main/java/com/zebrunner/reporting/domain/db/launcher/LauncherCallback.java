package com.zebrunner.reporting.domain.db.launcher;

import com.zebrunner.reporting.domain.db.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LauncherCallback extends AbstractEntity {

    private String ref;
    private String ciRunId;
    private String url;
    private LauncherPreset preset;

    public LauncherCallback(String ciRunId, String url, LauncherPreset preset) {
        this.ciRunId = ciRunId;
        this.url = url;
        this.preset = preset;
    }
}
