package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LauncherPresetDTO extends AbstractType {

    @NotEmpty(message = "Name required")
    @Size(max = 50, message = "Name length should include between 1 and 50 characters")
    private String name;
    private String params;
}
