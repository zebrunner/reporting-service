package com.zebrunner.reporting.service.email;

public class UserInviteEmail extends AbstractEmail {

    private static final String SUBJECT = "Join the workspace";

    private final String logoUrl;
    private final String workspaceURL;
    private final String invitationUrl;

    public UserInviteEmail(String invitationUrl, String logoUrl, String workspaceURL) {
        super(SUBJECT, EmailType.USER_INVITE, logoUrl, workspaceURL);
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
