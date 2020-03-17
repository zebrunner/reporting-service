package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScmAccount extends AbstractEntity {

    private static final long serialVersionUID = 7205460418919094068L;

    private String login;
    private String accessToken;
    private String organizationName;
    private String repositoryName;
    private String avatarURL;
    private String repositoryURL;
    private String apiVersion;
    private Long userId;
    private Name name;

    public enum Name {
        GITHUB, GITHUB_ENTERPRISE
    }

    public ScmAccount(String accessToken, Name name) {
        this.accessToken = accessToken;
        this.name = name;
    }

    public ScmAccount(String organizationName, String repositoryName) {
        this.organizationName = organizationName;
        this.repositoryName = repositoryName;
    }

    @Override
    public int hashCode() {
        return (this.organizationName + this.repositoryURL).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ScmAccount && this.hashCode() == obj.hashCode();
    }

    public String buildAuthorizedURL(String decryptedAccessToken) {
        String[] urlSlices = repositoryURL.split("//");
        return urlSlices[0] + "//" + decryptedAccessToken + "@" + urlSlices[1];
    }

}
