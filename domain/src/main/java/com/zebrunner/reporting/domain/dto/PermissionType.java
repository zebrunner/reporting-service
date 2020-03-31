package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.Permission;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PermissionType extends AbstractType {

    private static final long serialVersionUID = 7083932006258442862L;

    @NotEmpty(message = "Name required")
    private Permission.Name name;

    @NotNull(message = "Block required")
    private Permission.Block block;

}
