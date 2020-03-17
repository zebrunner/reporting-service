package com.zebrunner.reporting.web.security.filter;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.dto.auth.JwtUserType;
import com.zebrunner.reporting.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtTokenAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private JWTService jwtService;

    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/**");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!requiresAuthentication(request)) {
            /*
             * if the URL requested doesn't match the URL handled by the filter, then we chain to the next filters.
             */
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            /*
             * If there's not authentication information, then we chain to the next filters. The SecurityContext will be
             * analyzed by the chained filter that will throw AuthenticationExceptions if necessary
             */
            chain.doFilter(request, response);
            return;
        }

        try {
            /*
             * The token is extracted from the header. It's then checked (signature and expiration) An Authentication is
             * then created and registered in the SecurityContext. The SecurityContext will be analyzed by chained
             * filters that will throw Exceptions if necessary (like if authorizations are incorrect).
             */
            User user = extractAndDecodeJwt(request);

            if (user.getStatus().equals(User.Status.INACTIVE)) {
                chain.doFilter(request, response);
                return;
            }

            Authentication auth = buildAuthenticationFromJwt(user, request);
            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException ex) {
            throw new BadCredentialsException("Token is expired or malformed");
        }

        /* SecurityContext is then cleared since we are stateless. */
        SecurityContextHolder.clearContext();
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    private User extractAndDecodeJwt(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        String token = authHeader.substring("Bearer ".length());
        return jwtService.parseAuthToken(token);
    }

    private Authentication buildAuthenticationFromJwt(User user, HttpServletRequest request) {
        JwtUserType userDetails = new JwtUserType(user.getId(), user.getUsername(), user.getGroups());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }
}