package com.zebrunner.reporting.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Exception is dedicated to integrations structure and wraps all specific for integrations
 * exceptions.
 * Reserved range 2200 - 2249
 */
@Getter
public class IntegrationException extends ApplicationException {

    private Map<String, String> additionalInfo;

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum IntegrationExceptionDetail implements ErrorDetail {

        JENKINS_SERVER_INITIALIZATION_FAILED(2200);

        private final Integer code;
        private String messageKey;

    }

    public IntegrationException() {
        super();
    }

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, Map<String, String> additionalInfo) {
        this(message);
        this.additionalInfo = additionalInfo;
    }

    public IntegrationException(Throwable cause) {
        super(cause);
    }

    public IntegrationException(ErrorDetail errorDetail, String message, Throwable cause) {
        super(errorDetail, message, cause);
    }

    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
