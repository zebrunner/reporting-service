package com.zebrunner.reporting.domain.dto.auth;

import com.zebrunner.reporting.domain.db.Permission;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
public class TenantAuth {

    @NotEmpty
    private String tenantName;

    @NotEmpty
    private String token;

    @NotEmpty
    private Set<Permission.Name> permissions;

}
