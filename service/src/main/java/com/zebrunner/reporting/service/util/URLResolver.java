package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.persistence.utils.TenancyContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class URLResolver {

    private static final String SIGNUP_PATH_PATTERN = "%s/signup?token=%s";

    @Value("${service.multitenant}")
    private boolean isMultitenant;

    @Value("${service.web-url}")
    private String webURL;

    @Value("${service.api-url}")
    private String webserviceURL;

    /**
     * Build service web url. In case of multitenant deployment subdomain will be included
     *
     * @return web URL
     */
    public String buildWebURL() {
        return isMultitenant ? String.format(webURL, TenancyContext.getTenantName()) : webURL;
    }

    public String buildWebserviceUrl() {
        return isMultitenant ? webserviceURL.replace("api", TenancyContext.getTenantName()) : webserviceURL;
    }

    public String getServiceURL() {
        return buildWebURL();
    }

    public String buildInvitationUrl(String token) {
        return String.format(SIGNUP_PATH_PATTERN, buildWebURL(), token);
    }

}
