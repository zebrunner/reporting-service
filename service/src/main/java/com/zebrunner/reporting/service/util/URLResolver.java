package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.persistence.utils.TenancyContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class URLResolver {

    private static final String SIGNUP_PATH_PATTERN = "%s/signup?token=%s";

    @Value("${zafira.multitenant}")
    private boolean isMultitenant;

    @Value("${zafira.web-url}")
    private String webURL;

    @Value("${zafira.api-url}")
    private String webserviceURL;

    /**
     * In case if multitenancy will resolve current tenancy id into the URL pattern: http://demo.qaprosoft.com/zafira.
     *
     * @return Zafira web URL
     */
    public String buildWebURL() {
        return isMultitenant ? String.format(webURL, TenancyContext.getTenantName()) : webURL;
    }

    public String buildWebserviceUrl() {
        return isMultitenant ? webserviceURL.replace("api", TenancyContext.getTenantName()) : webserviceURL;
    }

    public String getServiceURL() {
        return getUrlFromWebUrl(buildWebURL());
    }

    public String buildInvitationUrl(String token) {
        return String.format(SIGNUP_PATH_PATTERN, buildWebURL(), token);
    }

    private static String getUrlFromWebUrl(String webUrl) {
        String result = null;
        Matcher matcher = Pattern.compile("^.+(?=/)").matcher(webUrl);
        while (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }


}
