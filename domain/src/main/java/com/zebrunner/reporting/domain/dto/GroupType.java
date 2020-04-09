package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.Group;
import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.domain.db.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class GroupType extends AbstractType {

    private static final long serialVersionUID = 4257992439033566293L;

    @NotEmpty(message = "Name required")
    private String name;

    @NotEmpty(message = "Role required")
    private Group.Role role;
    private Boolean invitable;
    private List<User> users;
    private Set<Permission> permissions;

}
