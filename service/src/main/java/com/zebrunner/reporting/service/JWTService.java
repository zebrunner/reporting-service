package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.dto.auth.AuthenticationTokenContent;
import com.zebrunner.reporting.domain.db.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JWTService {

    public static final String AUTHENTICATION_TOKEN_CLAIM_USERNAME = "username";
    public static final String AUTHENTICATION_TOKEN_CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_TENANT = "tenant";

    private final String secret;
    private final Integer authTokenExp;

    public JWTService(@Value("${auth.token.secret}") String secret,
                      @Value("${auth.token.expiration}") Integer authTokenExp) {
        this.authTokenExp = authTokenExp;
        this.secret = secret;
    }

    public String generateAuthenticationToken(Integer userId, String username, String tenantName, Set<String> permissions) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        claims.put(AUTHENTICATION_TOKEN_CLAIM_USERNAME, username);
        claims.put(AUTHENTICATION_TOKEN_CLAIM_PERMISSIONS, permissions);
        claims.put(CLAIM_TENANT, tenantName);
        return buildToken(claims, authTokenExp);
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
        return new AuthenticationTokenContent(userId, username, tenant, new HashSet<>(permissions));
    }

    private String buildToken(Claims claims, Integer exp) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, exp);
        return Jwts.builder()
                   .setClaims(claims)
                   .signWith(SignatureAlgorithm.HS512, secret)
                   .setExpiration(calendar.getTime())
                   .compact();
    }

    private Claims getTokenBody(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

}