package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.dto.auth.AuthenticationTokenContent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class JWTService {

    public static final String AUTHENTICATION_TOKEN_CLAIM_USERNAME = "username";
    public static final String CLAIM_TENANT = "tenant";

    private final String secret;

    public JWTService(@Value("${auth.token.secret}") String secret) {
        this.secret = secret;
    }

    /**
     * Parses user details from JWT token.
     * 
     * @param token
     *            - to parse
     * @return retrieved user details
     */
    @SuppressWarnings("unchecked")
    public AuthenticationTokenContent parseAuthToken(String token) {
        Claims jwtBody = getTokenBody(token);
        Integer userId = Integer.valueOf(jwtBody.getSubject());
        String username = jwtBody.get(AUTHENTICATION_TOKEN_CLAIM_USERNAME, String.class);
        String tenant = jwtBody.get(CLAIM_TENANT, String.class);
        List<String> permissions = (List<String>)jwtBody.get("permissions");
        return new AuthenticationTokenContent(userId, username, tenant, new HashSet<>(permissions), token);
    }

    private Claims getTokenBody(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

}