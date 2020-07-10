package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.dto.auth.AuthenticatedUser;
import com.zebrunner.reporting.domain.push.AbstractPush;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AbstractController {

    protected String getStatisticsWebsocketPath() {
        return buildWebSocketPath(AbstractPush.Type.TEST_RUN_STATISTICS, TenancyContext.getTenantName());
    }

    protected String getTestRunsWebsocketPath() {
        return buildWebSocketPath(AbstractPush.Type.TEST_RUN, TenancyContext.getTenantName());
    }

    protected String getTestsWebsocketPath(Long testRunId) {
        return buildWebSocketPath(AbstractPush.Type.TEST, TenancyContext.getTenantName(), testRunId);
    }

    protected String getLaunchersWebsocketPath() {
        return buildWebSocketPath(AbstractPush.Type.LAUNCHER, TenancyContext.getTenantName());
    }

    protected String getLauncherRunsWebsocketPath() {
        return buildWebSocketPath(AbstractPush.Type.LAUNCHER_RUN, TenancyContext.getTenantName());
    }

    private String buildWebSocketPath(AbstractPush.Type type, Object... parameters) {
        return type.buildWebsocketPath(parameters);
    }

    private AuthenticatedUser getPrincipal() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user instanceof AuthenticatedUser ? (AuthenticatedUser) user : null;
    }

    protected Integer getPrincipalId() {
        AuthenticatedUser user = getPrincipal();
        return user != null ? user.getId() : 0;
    }

    protected String getPrincipalName() {
        UserDetails user = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return user != null ? user.getUsername() : "";
    }

    protected boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    protected boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            return authentication.getAuthorities().stream()
                                 .flatMap(grantedAuthority -> ((AuthenticatedUser) authentication.getPrincipal()).getPermissions().stream())
                                 .anyMatch(p -> p.equalsIgnoreCase(permission));
        }
        return false;
    }
}
