package com.zebrunner.reporting.domain.dto.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Error {

    @JsonInclude(Include.NON_EMPTY)
    private String field;
    @JsonInclude(Include.NON_EMPTY)
    private String message;
    @JsonInclude(Include.NON_NULL)
    private ErrorCode code;
    @JsonInclude(Include.NON_NULL)
    private AdditionalErrorData additional;

    public Error(ErrorCode code) {
        this.code = code;
    }

    public Error(ErrorCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public Error(ErrorCode code, String field, String message) {
        this.code = code;
        this.field = field;
        this.message = message;
    }

}
