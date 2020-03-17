package com.zebrunner.reporting.domain.dto.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AccessTokenDTO implements Serializable {
    private static final long serialVersionUID = 2982073032065087590L;

    public AccessTokenDTO() {
    }

    public AccessTokenDTO(String token) {
        this.token = token;
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
