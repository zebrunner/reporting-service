package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zebrunner.reporting.domain.dto.utils.validation.Json;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LauncherPresetDTO extends AbstractType {

    @NotEmpty(message = "Name required")
    @Size(max = 50, message = "Name length should include between 1 and 50 characters")
    private String name;

    @Json(message = "Params string should contains JSON")
    private String params;

    @Positive(message = "Test environment provider id shoul be positive number")
    private Long providerId;
}
