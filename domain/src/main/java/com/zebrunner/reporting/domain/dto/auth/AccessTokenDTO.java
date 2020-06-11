package com.zebrunner.reporting.domain.dto.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Value;

@JsonInclude(Include.NON_NULL)
@Value
public class AccessTokenDTO implements Serializable {

    private String token;

}
