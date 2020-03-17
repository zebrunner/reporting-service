package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class User extends AbstractEntity implements Comparable<User> {

    private static final long serialVersionUID = 2720141152633805371L;

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String photoURL;
    private List<Group> groups = new ArrayList<>();
    private List<UserPreference> preferences = new ArrayList<>();
    private Date lastLogin;
    private String tenant;
    private Source source;
    private Status status;
    private String resetToken;

    public User() {
    }

    public enum Source {
        INTERNAL,
        LDAP
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public User(long id) {
        super.setId(id);
    }

    public User(String username) {
        this.username = username;
    }

    public void setRoles(List<Group.Role> roles) {
        // Do nothing just treak for dozer mapper
    }

    public List<Group.Role> getRoles() {
        Set<Group.Role> roles = new HashSet<>();
        for (Group group : groups) {
            roles.add(group.getRole());
        }
        return new ArrayList<>(roles);
    }

    public Set<Permission> getPermissions() {
        return this.groups.stream().flatMap(group -> group.getPermissions().stream())
                .collect(Collectors.toSet());
    }

    public List<Group> getGrantedGroups() {
        this.groups.forEach(group -> {
            group.setUsers(null);
            group.setId(null);
            group.setCreatedAt(null);
            group.setModifiedAt(null);
            group.getPermissions().forEach(permission -> permission.setId(null));
        });
        return this.groups;
    }

    @Override
    public int compareTo(User user) {
        return username.compareTo(user.getUsername());
    }

}
