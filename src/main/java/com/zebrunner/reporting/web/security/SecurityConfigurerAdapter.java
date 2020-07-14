package com.zebrunner.reporting.web.security;

import com.zebrunner.reporting.web.security.filter.CORSFilter;
import com.zebrunner.reporting.web.security.filter.JwtTokenAuthenticationFilter;
import com.zebrunner.reporting.web.security.filter.RestAccessDeniedHandler;
import com.zebrunner.reporting.web.security.filter.SecurityAuthenticationEntryPoint;
import com.zebrunner.reporting.web.security.filter.TenancyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_API_PATTERNS = new String[]{
            "/api/config/**",
            "/api/status/**",
            "/api/dashboards/email",
            "/api/settings/companyLogo",
            "/api/websockets/**",
            "/api/launchers/hooks/*",
            "/api/integrations-info/public",
            "/api/social/**",
            "/api/auth/tenant/**"
    };

    private static final String[] AUTHENTICATED_API_PATTERNS = new String[]{
            "/api/users/**",
            "/api/filters/**",
            "/api/profiles/**",
            "/api/tests/**",
            "/api/dashboards/**",
            "/api/widgets/**",
            "/api/projects/**",
            "/api/devices/**",
            "/api/settings/**",
            "/api/jobs/**",
            "/api/certification/**",
            "/api/events/**",
            "/api/projects/**",
            "/api/slack/**",
            "/api/scm/**",
            "/api/launchers/**",
            "/api/integrations/**",
            "/api/security/**",
            "/api/tests/sessions/**",
            "/v1/test-runs/**",
            "/v1/test-sessions/**"
    };

    private final TenancyFilter tenancyFilter;
    private final CORSFilter corsFilter = new CORSFilter();
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;
    private final SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(securityAuthenticationEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(restAccessDeniedHandler)
                .and()
                .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
                .addFilterBefore(tenancyFilter, CORSFilter.class)
                .addFilterAfter(jwtTokenAuthenticationFilter, ExceptionTranslationFilter.class)
                .authorizeRequests()
                .antMatchers(PUBLIC_API_PATTERNS).permitAll()
                .antMatchers(AUTHENTICATED_API_PATTERNS).authenticated();
    }

    @Bean
    public FilterRegistrationBean<JwtTokenAuthenticationFilter> jwtTokenAuthenticationFilterRegistration(JwtTokenAuthenticationFilter filter) {
        FilterRegistrationBean<JwtTokenAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<TenancyFilter> tenancyFilterRegistration(TenancyFilter filter) {
        FilterRegistrationBean<TenancyFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

}
