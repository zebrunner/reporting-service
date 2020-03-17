package com.zebrunner.reporting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

    @Value("${zafira.version}")
    private String serviceVersion;

    @Value("${zafira.client-version}")
    private String clientVersion;

    @Value("${zafira.api-url}")
    private String webserviceURL;

    public String getServiceVersion() {
        return serviceVersion;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public String getWebserviceURL() {
        return webserviceURL;
    }

}