package com.zebrunner.reporting.service.feign;

import com.zebrunner.reporting.domain.dto.auth.AccessTokenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "iamAuthClient", url = "http://${iam.host}:${iam.port}")
@RequestMapping(path = "/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public interface IamAuthClient {

    @GetMapping("/access")
    AccessTokenDTO getServiceRefreshToken();

}