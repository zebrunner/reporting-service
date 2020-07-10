package com.zebrunner.reporting.web.security.expressions;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

/**
 * Allows access for {@link RestMethodSecurityExpressionRoot} (registers)
 * 
 * @author Bogdan Rutskov
 */
public class RestMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private static final AuthenticationTrustResolver trustResolver;

    static {
        trustResolver = new AuthenticationTrustResolverImpl();
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
            MethodInvocation invocation) {
        RestMethodSecurityExpressionRoot root = new RestMethodSecurityExpressionRoot(authentication,
                (UserPermissionEvaluator) getPermissionEvaluator());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
