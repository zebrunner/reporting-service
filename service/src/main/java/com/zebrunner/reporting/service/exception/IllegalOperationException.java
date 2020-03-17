package com.zebrunner.reporting.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exception that is thrown to indicate that certain operation is not valid according to business logic of application
 * (e.g. when someone attempts to update recourse with a certain status that indicates immutable state at given moment -
 * simply put can not be updated).
 * Reserved range 2060 - 2099
 */
public class IllegalOperationException extends ApplicationException {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum IllegalOperationErrorDetail implements ErrorDetail {

        USER_CAN_NOT_BE_CREATED(2060),
        DASHBOARD_CAN_NOT_BE_CREATED(2061),
        FILTER_CAN_NOT_BE_CREATED(2062),
        INTEGRATION_CAN_NOT_BE_CREATED(2063),
        JOB_CAN_NOT_BE_STARTED(2064),
        INVITATION_CAN_NOT_BE_CREATED(2065),
        ILLEGAL_FILTER_ACCESS(2066),
        TEST_RUN_CAN_NOT_BE_STARTED(2067),
        LAUNCHER_PRESET_CAN_NOT_BE_CREATED(2068),
        ILLEGAL_TEST_RUN_ACTION_BY_ID(2069),
        INVITATION_IS_INVALID(2070),
        TOKEN_RESET_IS_NOT_POSSIBLE(2071),
        CREDENTIALS_RESET_IS_NOT_POSSIBLE(2072),
        CHANGE_PASSWORD_IS_NOT_POSSIBLE(2073),
        DASHBOARD_CAN_NOT_BE_UPDATED(2074),
        GROUP_CAN_NOT_BE_DELETED(2075),
        ATTACHMENT_RESOURCE_NOT_NULL(2076),
        TEST_RUN_RERUN_CAN_NOT_BE_STARTED(2077),
        WORK_ITEM_CAN_NOT_BE_ATTACHED(2078),
        INVALID_IMAGE_FILE(2079),
        TOKEN_REFRESH_IS_NOT_SUPPORTED(2080);

        private final Integer code;
        private String messageKey;

    }

    public IllegalOperationException(IllegalOperationErrorDetail errorDetail, String message) {
        super(errorDetail, message);
    }

    public IllegalOperationException(IllegalOperationErrorDetail errorDetail, String message, Object... args) {
        super(errorDetail, message, args);
    }

}
