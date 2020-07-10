package com.zebrunner.reporting.web.security.expressions;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Add possibility to use hasPermission('expression') and hasAnyPermission('expressions') method expression (by default is not exist)
 * 
 * @author Bogdan Rutskov
 */
public class RestMethodSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestMethodSecurityExpressionRoot.class);

    private UserPermissionEvaluator permissionEvaluator;

    public RestMethodSecurityExpressionRoot(Authentication authentication, UserPermissionEvaluator permissionEvaluator) {
        super(authentication);
        this.permissionEvaluator = permissionEvaluator;
    }

    public boolean hasPermission(String permission) {
        return permissionEvaluator.hasPermission(super.authentication, null, permission);
    }

    public boolean hasAnyPermission(String... permissions) {
        return permissionEvaluator.hasAnyPermission(super.authentication, permissions);
    }

    public boolean isOwner(Object targetDomainObject, String fieldName) {
        boolean result = false;
        try {
            result = targetDomainObject != null && permissionEvaluator
                    .isOwner(super.authentication, FieldUtils.readDeclaredField(targetDomainObject, fieldName, true));
        } catch (IllegalAccessException e) {
            LOGGER.debug(e.getMessage());
        }
        return result;
    }

    @Override
    public void setFilterObject(Object filterObject) {
    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {

    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }
}
