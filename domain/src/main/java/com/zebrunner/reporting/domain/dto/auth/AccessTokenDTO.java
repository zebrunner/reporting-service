package com.zebrunner.reporting.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@Getter
public class AccessTokenDTO {

    private String token;

}
