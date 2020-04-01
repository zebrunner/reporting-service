package com.zebrunner.reporting.service.email;

public class ResetPasswordLdapEmail extends AbstractEmail {

    private static final String SUBJECT = "Password reset";

    public ResetPasswordLdapEmail(String logoUrl, String workspaceURL) {
        super(SUBJECT, EmailType.FORGOT_PASSWORD_LDAP, logoUrl, workspaceURL);
    }

}
