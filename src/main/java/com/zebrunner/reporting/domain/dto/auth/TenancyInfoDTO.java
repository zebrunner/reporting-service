package com.zebrunner.reporting.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenancyInfoDTO {

    private String tenant;
    private String serviceUrl;
    private boolean useArtifactsProxy;
    private boolean multitenant;

    public TenancyInfoDTO(String tenant) {
        this.tenant = tenant;
    }

}
