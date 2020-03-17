package com.zebrunner.reporting.web.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zebrunner.reporting.domain.dto.errors.Error;
import com.zebrunner.reporting.domain.dto.errors.ErrorCode;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * SecurityAuthenticationEntryPoint is called by ExceptionTranslationFilter to handle all AuthenticationException. These
 * exceptions are thrown when authentication failed : wrong login/password, authentication unavailable, invalid token
 * authentication expired, etc.
 *
 * For problems related to access (roles), see RestAccessDeniedHandler.
 */
@Component
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorResponse result = new ErrorResponse();
        result.setError(new Error(ErrorCode.UNAUTHORIZED));
        ObjectMapper objMapper = new ObjectMapper();

        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
        wrapper.setStatus(SC_UNAUTHORIZED);
        wrapper.setContentType(APPLICATION_JSON_VALUE);
        wrapper.getWriter().println(objMapper.writeValueAsString(result));
        wrapper.getWriter().flush();
    }
}