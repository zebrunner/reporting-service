package com.zebrunner.reporting.service.client;

import com.zebrunner.reporting.domain.dto.auth.AccessTokenDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "iamServiceClient", url = "${iam.service-url}")
@RequestMapping(path = "/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public interface IamServiceClient {

//    @RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}", produces = "application/json")
//    Post getPostById(@PathVariable("postId") Long postId);

    @GetMapping("/access")
//    @Headers("Content-Type: application/json")
    AccessTokenDTO getServiceRefreshToken();
}