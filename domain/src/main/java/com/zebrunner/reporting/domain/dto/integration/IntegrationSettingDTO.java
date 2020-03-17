package com.zebrunner.reporting.domain.dto.integration;

import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class IntegrationSettingDTO extends AbstractType {

    @NotNull(message = "Value required")
    private String value;
    private byte[] binaryData;
    private boolean encrypted;

    @Valid
    @NotNull(message = "Integration params required")
    private IntegrationParamDTO param;

}
