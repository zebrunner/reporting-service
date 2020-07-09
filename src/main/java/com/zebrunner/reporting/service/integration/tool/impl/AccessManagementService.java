package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.accessmanagement.AccessManagementAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.AccessManagementProxy;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.stereotype.Component;

@Component
public class AccessManagementService extends AbstractIntegrationService<AccessManagementAdapter> {

    public AccessManagementService(IntegrationService integrationService, AccessManagementProxy accessManagementProxy) {
        super(integrationService, accessManagementProxy, "LDAP");
    }

    public boolean isUserExists(String username) {
        AccessManagementAdapter adapter = getDefaultAdapterByType();
        return adapter.isUserExists(username);
    }

    public BindAuthenticator getBindAuthenticator() {
        AccessManagementAdapter adapter = getDefaultAdapterByType();
        return adapter.getBindAuthenticator();
    }
}
