package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.entity.integration.IntegrationInfo;
import com.zebrunner.reporting.domain.entity.integration.IntegrationPublicInfo;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.web.documented.IntegrationInfoDocumentedController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RequestMapping(path = "api/integrations-info", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class IntegrationInfoController extends AbstractController implements IntegrationInfoDocumentedController {

    private final IntegrationService integrationService;

    public IntegrationInfoController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping()
    @Override
    public Map<String, Map<String, List<IntegrationInfo>>> getIntegrationsInfo() {
        return integrationService.retrieveInfo();
    }

    @GetMapping("/public")
    @Override
    public List<IntegrationPublicInfo> getPublicInfos() {
        return integrationService.retrievePublicInfo();
    }

    @GetMapping("/{id}")
    @Override
    public IntegrationInfo getIntegrationsInfoById(@PathVariable("id") Long id, @RequestParam("groupName") String groupName) {
        return integrationService.retrieveInfoByIntegrationId(groupName, id);
    }
}
