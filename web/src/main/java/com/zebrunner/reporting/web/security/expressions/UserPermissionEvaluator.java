package com.zebrunner.reporting.web.security.expressions;

import com.zebrunner.reporting.domain.dto.auth.JwtUserType;
import com.zebrunner.reporting.domain.dto.auth.UserGrantedAuthority;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Arrays;
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
            return ((JwtUserType) authentication.getPrincipal()).getId() == (Long) targetDomainObject;
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        return hasPermission(authentication, targetType, permission);
    }

    private boolean checkAuthority(Authentication authentication, Predicate<String> permissionsPredicate) {
        return authentication.getAuthorities().stream()
                .flatMap(grantedAuthority -> ((UserGrantedAuthority) grantedAuthority).getPermissions().stream())
                .anyMatch(permissionsPredicate);
    }
}
