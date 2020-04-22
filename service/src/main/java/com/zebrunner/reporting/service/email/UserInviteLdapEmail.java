package com.zebrunner.reporting.service.email;

public class UserInviteLdapEmail extends AbstractEmail {

    private static final String SUBJECT = "Join the workspace";

    private final String logoUrl;
    private final String workspaceURL;
    private final String invitationUrl;

    public UserInviteLdapEmail(String invitationUrl, String logoUrl, String workspaceURL) {
        super(SUBJECT, EmailType.USER_INVITE_LDAP, logoUrl, workspaceURL);
        this.invitationUrl = invitationUrl;
        this.logoUrl = logoUrl;
        this.workspaceURL = workspaceURL;
    }

    public String getInvitationUrl() {
        return invitationUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getWorkspaceURL() {
        return workspaceURL;
    }

}
