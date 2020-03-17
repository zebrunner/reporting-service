package com.zebrunner.reporting.web.util.swagger;

import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = { @io.swagger.annotations.ApiResponse(code = 200, message = "OK"),
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad request"),
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden"),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found"),
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server error") })
public @interface ApiResponseStatuses {
}
