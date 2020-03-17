package com.zebrunner.reporting.service.email;

public class UserInviteEmail extends AbstractEmail {

    private static final String SUBJECT = "Join the workspace";

    private final String zafiraLogoURL;
    private final String workspaceURL;
    private final String invitationUrl;

    public UserInviteEmail(String invitationUrl, String zafiraLogoURL, String workspaceURL) {
        super(SUBJECT, EmailType.USER_INVITE, zafiraLogoURL, workspaceURL);
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
