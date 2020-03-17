package com.zebrunner.reporting.domain.dto.scm;

import java.io.Serializable;

public class Repository implements Serializable {

    private static final long serialVersionUID = 5674477137924671883L;

    private String name;
    private String url;
    private String defaultBranch;
    private Boolean isPrivate;

    public Repository() {
    }

    public Repository(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

}
