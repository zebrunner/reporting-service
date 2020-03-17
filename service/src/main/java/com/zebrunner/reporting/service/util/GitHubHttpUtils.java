package com.zebrunner.reporting.service.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class GitHubHttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubHttpUtils.class);

    private static final String GITHUB_ACCESS_TOKEN_PATH = "https://github.com/login/oauth/access_token";

    private CloseableHttpClient httpClient;

    public GitHubHttpUtils() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    public String getAccessToken(String code, String clientId, String secret) throws URISyntaxException, IOException {
        HttpResponse httpResponse = this.httpClient.execute(buildGetAccessTokenRequest(code, clientId, secret));
        return getAccessToken(EntityUtils.toString(httpResponse.getEntity()));
    }

    private static HttpPost buildGetAccessTokenRequest(String code, String clientId, String secret) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(GITHUB_ACCESS_TOKEN_PATH);
        uriBuilder.addParameter("client_id", clientId)
                .addParameter("client_secret", secret)
                .addParameter("code", code)
                .addParameter("accept", "json");
        return new HttpPost(uriBuilder.build());
    }

    private String getAccessToken(String response) {
        return response.split("access_token=")[1].split("&")[0];
    }

    @PreDestroy
    public void close() {
        try {
            if (httpClient != null)
                this.httpClient.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
