package com.zebrunner.reporting.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exception that is meant to be thrown when we get a technical runtime error from integrated system
 * (e.g. connection timeout).
 * Reserved range 2150 - 2199
 */
public class ExternalSystemException extends ApplicationException {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum ExternalSystemErrorDetail implements ErrorDetail {

        JIRA_ISSUE_CAN_NOT_BE_FOUND(2150),
        POSTGRES_VERSION_CAN_NOT_BE_FOUND(2151),
        LDAP_USER_DOES_NOT_EXIST(2152),
        LDAP_AUTHENTICATION_FAILED(2153),
        JENKINS_JOB_DOES_NOT_EXIST(2155),
        JENKINS_BUILD_DOES_NOT_EXIST(2156),
        JENKINS_QUEUE_REFERENCE_IS_NOT_OBTAINED(2157),
        GITHUB_AUTHENTICATION_FAILED(2158),
        GITHUB_DEFAULT_BRANCH_IS_NOT_OBTAINED(2159);

        private final Integer code;
        private String messageKey;

    }

    public ExternalSystemException(ExternalSystemErrorDetail errorDetail, String message, Throwable cause) {
        super(errorDetail, message, cause);
    }
    public ExternalSystemException(ExternalSystemErrorDetail errorDetail, String message, Object... args) {
        super(errorDetail, message, args);
    }

    public ExternalSystemException(ExternalSystemErrorDetail errorDetail, String message) {
        super(errorDetail, message);
    }

    public ExternalSystemException(String message, Throwable cause) {
        super(message, cause);
    }

}
