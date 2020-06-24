package com.zebrunner.reporting.service.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zebrunner.reporting.domain.db.ScmAccount;
import com.zebrunner.reporting.domain.dto.scm.ScmConfig;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
@Slf4j
public class GitHubClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubClient.class);

    private static final String GITHUB_ACCESS_TOKEN_PATH = "https://%s/login/oauth/access_token";
    private static final String GITHUB_ENTERPRISE_API = "https://%s/api/v3";
    private static final String GITHUB_API = "https://api.%s";

    @Setter(onMethod = @__(@Value("${github.client-id}")))
    private String gitHubClientId;

    @Setter(onMethod = @__(@Value("${github.client-secret}")))
    private String gitHubSecret;

    @Setter(onMethod = @__(@Value("${github.host}")))
    private String gitHubHost;

    private final CloseableHttpClient httpClient;

    public GitHubClient() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    @SneakyThrows
    public String getAccessToken(String code) {
        URIBuilder uriBuilder = new URIBuilder(String.format(GITHUB_ACCESS_TOKEN_PATH, gitHubHost));
        uriBuilder.addParameter("client_id", gitHubClientId)
                  .addParameter("client_secret", gitHubSecret)
                  .addParameter("code", code)
                  .addParameter("accept", "json");

        HttpPost getAccessTokenRequest = new HttpPost(uriBuilder.build());
        getAccessTokenRequest.addHeader("Accept", "application/json");

        HttpResponse httpResponse = this.httpClient.execute(getAccessTokenRequest);

        String response = EntityUtils.toString(httpResponse.getEntity());

        return parseValue(response, "access_token");
    }

    @SneakyThrows
    public String getUsername(String token) {
        URIBuilder uriBuilder = new URIBuilder(getApiVersion() + "/user");

        HttpGet userRequest = new HttpGet(uriBuilder.build());
        userRequest.addHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = this.httpClient.execute(userRequest);

        String response = EntityUtils.toString(httpResponse.getEntity());

        return parseValue(response, "login");
    }

    /**
     * Parses property from response body.
     *
     * @param response string response body representation
     * @param property property to get from response body
     * @return property value, null when property is not present
     */
    private String parseValue(String response, String property) {
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonElement accessToken = jsonObject.get(property);
        return accessToken != null ? accessToken.getAsString() : null;
    }

    public ScmConfig getConfig() {
        return new ScmConfig(gitHubClientId, gitHubHost);
    }

    /**
     * Based on account type returned internal ScmAccount.Name.
     *
     * @return ScmAccount.Name for provided github account.
     */
    public ScmAccount.Name getAccountName() {
        if (isEnterprise()) {
            return ScmAccount.Name.GITHUB_ENTERPRISE;
        }
        return ScmAccount.Name.GITHUB;
    }

    /**
     * Enterprise and not enterprise github api patterns differ,
     * depending on account type appropriate api version is chosen.
     *
     * @return api version for provided github account.
     */
    public String getApiVersion() {
        return isEnterprise() ? String.format(GITHUB_ENTERPRISE_API, gitHubHost) : String.format(GITHUB_API, gitHubHost);
    }

    /**
     * Enterprise github accounts have customized host name,
     * default github host name is compared with provided to find out account type.
     *
     * @return true when github host is custom.
     */
    private boolean isEnterprise() {
        return !"github.com".equals(gitHubHost);
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
