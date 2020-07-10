package com.zebrunner.reporting.domain.dto.integration;

import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class IntegrationDTO extends AbstractType {

    @NotNull(message = "Name required")
    @Size(min = 2, max = 50, message = "Name must to be between 2 and 50 symbols")
    private String name;
    private String backReferenceId;
    private boolean isDefault;
    private boolean enabled;
    private boolean connected;

    @Valid
    @NotEmpty(message = "Integration settings required")
    private List<IntegrationSettingDTO> settings;

    private IntegrationTypeDTO type;
}
