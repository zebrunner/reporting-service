package com.zebrunner.reporting.service.email;

public class UserInviteLdapEmail extends AbstractEmail {

    private static final String SUBJECT = "Join the workspace";

    private final String zafiraLogoURL;
    private final String workspaceURL;
    private final String invitationUrl;

    public UserInviteLdapEmail(String invitationUrl, String zafiraLogoURL, String workspaceURL) {
        super(SUBJECT, EmailType.USER_INVITE_LDAP, zafiraLogoURL, workspaceURL);
        this.invitationUrl = invitationUrl;
        this.zafiraLogoURL = zafiraLogoURL;
        this.workspaceURL = workspaceURL;
    }

    public String getInvitationUrl() {
        return invitationUrl;
    }

    public String getZafiraLogoURL() {
        return zafiraLogoURL;
    }

    public String getWorkspaceURL() {
        return workspaceURL;
    }

}
