package com.zebrunner.reporting.domain.dto.utils.validation;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;

public class JsonValidator implements ConstraintValidator<Json, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void initialize(Json constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || isJson(s);
    }

    private static boolean isJson(String str) {
        try {
            MAPPER.readTree(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
