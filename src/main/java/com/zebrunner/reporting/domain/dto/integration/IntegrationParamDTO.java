package com.zebrunner.reporting.domain.dto.integration;

import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class IntegrationParamDTO extends AbstractType {

    @NotNull(message = "Param name required")
    private String name;
    private String metadata;
    private String defaultValue;
    private boolean mandatory;
    private boolean needEncryption;

}
