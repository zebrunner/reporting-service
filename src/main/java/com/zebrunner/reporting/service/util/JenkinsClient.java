package com.zebrunner.reporting.service.util;

import com.offbytwo.jenkins.client.JenkinsHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.net.URI;

/**
 * JenkinsClient - wraps {@link JenkinsHttpClient} for HTTP timeout configuration.
 * 
 * @author akhursevich
 */
public class JenkinsClient extends JenkinsHttpClient {

    public JenkinsClient(URI uri, JenkinsConfig config) {
        super(uri, configureHttpClient(uri, config));
        HttpContext ctxt = new BasicHttpContext();
        ctxt.setAttribute("preemptive-auth", new BasicScheme());
        setLocalContext(ctxt);
    }

    /**
     * Allows to configure HTTP connection timeouts for save Jenkins integration.
     * 
     * @param uri - Jenkins URI
     * @return configured {@link HttpClientBuilder}
     */
    private static HttpClientBuilder configureHttpClient(URI uri, JenkinsConfig config) {
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(config.getTimeout());
        requestBuilder = requestBuilder.setConnectionRequestTimeout(config.getTimeout());

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());

        return addAuthentication(builder, uri, config.getUsername(), config.getPassword());
    }
}
