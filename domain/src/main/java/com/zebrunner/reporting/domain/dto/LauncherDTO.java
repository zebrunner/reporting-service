package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class LauncherDTO extends AbstractType {

    private static final long serialVersionUID = 7778329756348322538L;

    @NotEmpty(message = "{error.name.required}")
    private String name;

    @NotEmpty(message = "{error.model.required}")
    private String model;

    private String type;

    @NotNull
    @Valid
    private ScmAccountType scmAccountType;

    @Valid
    private List<LauncherPresetDTO> presets;

    private JobDTO job;
    private boolean autoScan;


}
