package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Group extends AbstractEntity {
    private static final long serialVersionUID = -1122566583572312653L;

    private String name;
    private Role role;
    private Boolean invitable;
    private List<User> users;
    private Set<Permission> permissions;

    public Group(String name, Role role, Set<Permission> permissions) {
        this.name = name;
        this.role = role;
        this.permissions = permissions;
    }

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }

    @JsonIgnore
    public Set<String> getPermissionNames() {
        return this.permissions.stream()
                               .map(permission -> permission.getName().name())
                               .collect(Collectors.toSet());
    }

    @JsonIgnore
    public boolean hasPermissions() {
        return this.permissions != null && this.permissions.size() > 0;
    }
}
