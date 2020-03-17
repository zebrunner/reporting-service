package com.zebrunner.reporting.domain.dto.errors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ErrorCodeSerializer.class)
public enum ErrorCode {

    VALIDATION_ERROR(0),
    INVALID_VALUE(1),
    INVALID_MIME_TYPE(2),

    UNAUTHORIZED(401),
    FORBIDDEN(403),

    INTERNAL_SERVER_ERROR(500),

    INVALID_TEST_RUN(1001),
    RESOURCE_NOT_FOUND(1002),
    TEST_RUN_NOT_REBUILT(1004),
    USER_NOT_FOUND(1005),
    ENTITY_ALREADY_EXISTS(1006),
    PROJECT_NOT_EXISTS(1008),

    INTEGRATION_UNAVAILABLE(2001),
    UNHEALTHY_STATUS(2002);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}