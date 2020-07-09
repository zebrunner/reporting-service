package com.zebrunner.reporting.domain.dto.integration;

import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IntegrationGroupDTO extends AbstractType {

    private String name;
    private String iconUrl;
    private String displayName;
    private boolean multipleAllowed;
    private List<IntegrationTypeDTO> types;

}
