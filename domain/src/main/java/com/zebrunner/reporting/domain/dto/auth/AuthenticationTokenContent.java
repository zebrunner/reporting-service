package com.zebrunner.reporting.domain.dto.auth;

import lombok.Value;

import java.util.Set;

@Value
public class AuthenticationTokenContent {

    Integer userId;
    String username;
    String tenantName;
    Set<String> permissions;

}
