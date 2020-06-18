package com.zebrunner.reporting.service.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailType {

    USER_INVITE("invitation.ftl"),
    USER_INVITE_LDAP("invitation_ldap.ftl"),
    DASHBOARD("dashboard.ftl"),
    FORGOT_PASSWORD("forgot_password.ftl"),
    FORGOT_PASSWORD_LDAP("forgot_password_ldap.ftl"),
    TEST_RUN("test_run_results.ftl");

    private final String templateName;

}