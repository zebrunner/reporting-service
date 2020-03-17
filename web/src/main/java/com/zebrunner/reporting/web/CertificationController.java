package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.dto.CertificationType;
import com.zebrunner.reporting.service.CertificationService;
import com.zebrunner.reporting.web.documented.CertificationDocumentedController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "api/certification", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class CertificationController extends AbstractController implements CertificationDocumentedController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping("/details")
    @Override
    public CertificationType getCertificationDetails(
            @RequestParam("upstreamJobId") Long upstreamJobId,
            @RequestParam("upstreamJobBuildNumber") Integer upstreamJobBuildNumber
    ) {
        return certificationService.getCertificationDetails(upstreamJobId, upstreamJobBuildNumber);
    }

}
