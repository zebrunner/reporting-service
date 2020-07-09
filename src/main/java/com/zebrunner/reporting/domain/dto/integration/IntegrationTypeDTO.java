package com.zebrunner.reporting.domain.dto.integration;

import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IntegrationTypeDTO extends AbstractType {

    private String name;
    private String iconUrl;
    private List<IntegrationParamDTO> params;

}
