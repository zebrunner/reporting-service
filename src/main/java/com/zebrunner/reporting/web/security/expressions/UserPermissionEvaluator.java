package com.zebrunner.reporting.web.security.expressions;

import com.zebrunner.reporting.domain.dto.auth.AuthenticatedUser;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Checks user permissions
 * 
 * @author Bogdan Rutskov
 */
public class UserPermissionEvaluator implements IUserPermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication != null && permission instanceof String) {
            boolean hasPermission = true;
            if (targetDomainObject != null) {
                hasPermission = (Boolean) targetDomainObject;
            }
            return checkAuthority(authentication, p -> p.equalsIgnoreCase(permission.toString())) && hasPermission;
        }
        return false;
    }

    @Override
    public boolean hasAnyPermission(Authentication authentication, String... permissions) {
        if (authentication != null) {
            return checkAuthority(authentication, p -> Arrays.asList(permissions).contains(p));
        }
        return false;
    }

    @Override
    public boolean isOwner(Authentication authentication, Object targetDomainObject) {
        if (authentication != null && targetDomainObject instanceof Long) {
            return ((AuthenticatedUser) authentication.getPrincipal()).getId() == (Integer) targetDomainObject;
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        return hasPermission(authentication, targetType, permission);
    }

    private boolean checkAuthority(Authentication authentication, Predicate<String> permissionsPredicate) {
        if (authentication.getPrincipal() instanceof AuthenticatedUser) {
            AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
            Set<String> permissions = authenticatedUser.getPermissions();
            return authentication.getAuthorities().stream()
                                 .flatMap(grantedAuthority -> permissions.stream())
                                 .anyMatch(permissionsPredicate);
        }
        return false;
    }
}
