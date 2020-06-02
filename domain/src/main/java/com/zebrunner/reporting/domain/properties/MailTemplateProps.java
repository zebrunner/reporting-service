package com.zebrunner.reporting.domain.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "mail-template")
public final class MailTemplateProps {

    private String dashboard;
    private String forgotPassword;
    private String forgotPasswordLdap;
    private String invitation;
    private String invitationLdap;
    private String testRunResult;

}
