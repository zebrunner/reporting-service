package com.zebrunner.reporting.service.email;

public class ResetPasswordEmail extends AbstractEmail {

    private static final String SUBJECT = "Password reset";

    private final String token;

    public ResetPasswordEmail(String token, String logoUrl, String workspaceURL) {
        super(SUBJECT, EmailType.FORGOT_PASSWORD, logoUrl, workspaceURL);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
