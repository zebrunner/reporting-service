package com.zebrunner.reporting.domain.db.launcher;

import com.zebrunner.reporting.domain.db.AbstractEntity;
import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.db.ScmAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Launcher extends AbstractEntity {

    private static final long serialVersionUID = 7864420961256586573L;

    private String name;
    private String model;
    private ScmAccount scmAccount;
    private Job job;
    private boolean autoScan;
    private List<LauncherPreset> presets;
    private String type;

    public Launcher(String name, String model, ScmAccount scmAccount, Job job, String type, boolean autoScan) {
        this.name = name;
        this.model = model;
        this.scmAccount = scmAccount;
        this.job = job;
        this.type = type;
        this.autoScan = autoScan;
    }
}
