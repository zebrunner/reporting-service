package com.zebrunner.reporting.web;

import com.zebrunner.reporting.service.ApplicationHealthService;
import com.zebrunner.reporting.web.documented.ApplicationHealthDocumentedController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "api/status", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ApplicationHealthController extends AbstractController implements ApplicationHealthDocumentedController {

    private final ApplicationHealthService applicationHealthService;

    public ApplicationHealthController(ApplicationHealthService applicationHealthService) {
        this.applicationHealthService = applicationHealthService;
    }

    @GetMapping()
    @Override
    public String getStatus() {
        return applicationHealthService.getStatus();
    }

}