package com.zebrunner.reporting.web.security.filter;

import com.google.common.net.InternetDomainName;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

/**
 * TenancyFilter - retrieves tenant by subdomain.
 * 
 * @author akhursevich
 */
@Component
public class TenancyFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenancyFilter.class);

    private static final String[] EXCLUSIONS = {"api/status"};

    @Value("${zafira.multitenant}")
    private boolean isMultitenant;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        if (Arrays.stream(EXCLUSIONS).noneMatch(path -> servletRequest.getRequestURI().contains(path))) {

            if (isMultitenant) {
                String host = servletRequest.getServerName(); // API clients without Origin
                String origin = servletRequest.getHeader("Origin"); // Web clients has Origin header
                if (!StringUtils.isEmpty(origin)) {
                    host = origin.split("//")[1].split(":")[0];
                }
                try {
                    InternetDomainName domain = InternetDomainName.from(host.replaceFirst("www.", ""));
                    if (!domain.isTopPrivateDomain() && domain.isUnderPublicSuffix()) {
                        String topDomain = domain.topPrivateDomain().toString();
                        String subDomain = domain.toString().replaceAll("." + topDomain, "");
                        TenancyContext.setTenantName(subDomain);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
