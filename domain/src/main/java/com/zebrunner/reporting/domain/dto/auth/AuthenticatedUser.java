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

    private Integer id;

    private String username;

    private String password;

    private Set<String> permissions;

    private String token;

    public AuthenticatedUser(Integer id, String username, Set<String> permissions, String token) {
        this.id = id;
        this.username = username;
        this.permissions = permissions;
        this.token = token;

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