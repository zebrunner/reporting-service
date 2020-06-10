package com.zebrunner.reporting.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {

    private long id;

    private String username;

    private String password;

    private Set<String> permissions;

    public AuthenticatedUser(long id, String username, Set<String> permissions) {
        this.id = id;
        this.username = username;
        this.permissions = permissions;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream()
                          .map(permission -> (GrantedAuthority) () -> permission)
                          .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}