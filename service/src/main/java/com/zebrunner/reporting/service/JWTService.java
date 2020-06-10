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

@Component
public class JWTService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTService.class);

    public static final String AUTHENTICATION_TOKEN_CLAIM_USERNAME = "username";
    public static final String REFRESH_TOKEN_CLAIM_PASSWORD = "password";
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
        return new AuthenticationTokenContent(userId, username, tenant, new HashSet<>(permissions));
    }

    /**
     * Generates JWT access token storing id, password of the user and specifies expiration (that never expires).
     * 
     * @param user
     *            - for token generation
     * @return generated JWT token
     */
    public String generateAccessToken(User user, String tenant) {
        Claims claims = Jwts.claims().setSubject(user.getId().toString());
//        claims.put(REFRESH_TOKEN_CLAIM_PASSWORD, user.getPassword());
        claims.put("tenant", tenant);
        return buildToken(claims, Integer.MAX_VALUE);
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