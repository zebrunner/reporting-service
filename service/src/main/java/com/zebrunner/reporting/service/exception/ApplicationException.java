package com.zebrunner.reporting.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * {@code ApplicationException} is the superclass of those exceptions that can be thrown during the normal operation
 * of the application. It should not be explicitly thrown - appropriate instance of one of its
 * subclasses should be thrown instead.
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

    public interface ErrorDetail {

        Integer getCode();

        String getMessageKey();

    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum UnexpectedErrorDetail implements ErrorDetail {

        UNEXPECTED_SERVER_ERROR(2666);

        private final Integer code;
        private String messageKey;

    }

    private ErrorDetail errorDetail;

    ApplicationException(ErrorDetail errorDetail, String message) {
        super(message);
        this.errorDetail = errorDetail;
    }

    ApplicationException(ErrorDetail errorDetail, String format, Object... args) {
        super(String.format(format, args));
        this.errorDetail = errorDetail;
    }

    ApplicationException(ErrorDetail errorDetail, String message, Throwable cause) {
        super(message, cause);
        this.errorDetail = errorDetail;
    }

    ApplicationException(ErrorDetail errorDetail, Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
        this.errorDetail = errorDetail;
    }

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message, Throwable cause, boolean writableStackTrace) {
        super(message, cause, false, writableStackTrace);
    }

}
