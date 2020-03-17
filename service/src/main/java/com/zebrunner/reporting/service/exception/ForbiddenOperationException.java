package com.zebrunner.reporting.service.exception;

public class ForbiddenOperationException extends ApplicationException {
    private static final long serialVersionUID = -1840720518398070678L;

    private boolean showMessage;

    public ForbiddenOperationException() {
        super();
    }

    public ForbiddenOperationException(String message) {
        super(message);
    }

    public ForbiddenOperationException(String message, boolean showMessage) {
        super(message);
        this.showMessage = showMessage;
    }

    public ForbiddenOperationException(Throwable cause) {
        super(cause);
    }

    public ForbiddenOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenOperationException(String message, Throwable cause, boolean writableStackTrace) {
        super(message, cause, writableStackTrace);
    }

    public boolean isShowMessage() {
        return showMessage;
    }
}