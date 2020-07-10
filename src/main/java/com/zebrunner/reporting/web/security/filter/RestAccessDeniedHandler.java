package com.zebrunner.reporting.web.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zebrunner.reporting.domain.dto.errors.Error;
import com.zebrunner.reporting.domain.dto.errors.ErrorCode;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * The RestAccessDeniedHandler is called by the ExceptionTranslationFilter to handle all AccessDeniedExceptions. These
 * exceptions are thrown when the authentication is valid but access is not authorized.
 */
@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        log.error("RestAccessDeniedHandler", accessDeniedException);
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.FORBIDDEN));
        ObjectMapper objMapper = new ObjectMapper();

        final HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
        wrapper.setStatus(HttpStatus.FORBIDDEN.value());
        wrapper.setContentType(APPLICATION_JSON_VALUE);
        wrapper.getWriter().println(objMapper.writeValueAsString(result));
        wrapper.getWriter().flush();
    }
}
