package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.dto.auth.TenancyInfoDTO;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.service.management.TenancyService;
import com.zebrunner.reporting.service.util.URLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final URLResolver urlResolver;
    private final TenancyService tenancyService;

    @GetMapping("/api/auth/tenant")
    public TenancyInfoDTO getTenancyInfo() {
        return new TenancyInfoDTO(
                TenancyContext.getTenantName(),
                urlResolver.getServiceURL(),
                tenancyService.isUseArtifactsProxy(),
                tenancyService.getIsMultitenant()
        );
    }

}
