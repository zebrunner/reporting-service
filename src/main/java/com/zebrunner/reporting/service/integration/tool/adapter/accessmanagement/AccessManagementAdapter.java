package com.zebrunner.reporting.service.integration.tool.adapter.accessmanagement;

import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;
import org.springframework.security.ldap.authentication.BindAuthenticator;

public interface AccessManagementAdapter extends IntegrationGroupAdapter {

    boolean isUserExists(String username);

    BindAuthenticator getBindAuthenticator();

}
