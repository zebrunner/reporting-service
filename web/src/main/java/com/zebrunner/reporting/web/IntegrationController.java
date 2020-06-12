package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;
import com.zebrunner.reporting.domain.dto.integration.IntegrationDTO;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.entity.integration.IntegrationInfo;
import com.zebrunner.reporting.service.StorageService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.web.documented.IntegrationDocumentedController;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RequestMapping(path = "api/integrations", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class IntegrationController extends AbstractController implements IntegrationDocumentedController {

    private final IntegrationService integrationService;
    private final StorageService storageService;
    private final Mapper mapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PostMapping()
    @Override
    public IntegrationDTO create(@RequestBody @Valid IntegrationDTO integrationDTO, @RequestParam("integrationTypeId") Long integrationTypeId) {
        Integration integration = mapper.map(integrationDTO, Integration.class);
        integration = integrationService.create(integration, integrationTypeId);

        IntegrationDTO integrationUpdateResultDTO =  mapper.map(integration, IntegrationDTO.class);

        IntegrationInfo integrationInfo = integrationService.retrieveInfoByIntegration(integration);
        integrationUpdateResultDTO.setConnected(integrationInfo.isConnected());

        return integrationUpdateResultDTO;
    }

    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS') or isAuthenticated()")
    @GetMapping()
    @Override
    public List<IntegrationDTO> getAll(
            @RequestParam(name = "groupId", required = false) Long groupId,
            @RequestParam(name = "groupName", required = false) String groupName
    ) {
        List<Integration> integrations;
        if (groupId != null) {
            integrations = integrationService.retrieveIntegrationsByGroupId(groupId);
        } else if (groupName != null) {
            integrations = integrationService.retrieveIntegrationsByGroupName(groupName);
        } else {
            integrations = integrationService.retrieveAll();
        }
        return integrations.stream()
                           .map(this::mapIntegration)
                           .collect(Collectors.toList());
    }

    private IntegrationDTO mapIntegration(Integration integration) {
        IntegrationDTO integrationDTO = mapper.map(integration, IntegrationDTO.class);
        if (integrationDTO.isEnabled()) {
            String gn = integration.getType().getGroup().getName();
            integrationDTO.setConnected(integrationService.isConnected(integration.getId(), gn));
        }
        return integrationDTO;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PutMapping("/{id}")
    @Override
    public IntegrationDTO update(@RequestBody IntegrationDTO integrationDTO, @PathVariable("id") Long id) {
        Integration integration = mapper.map(integrationDTO, Integration.class);
        integration.setId(id);

        integration = integrationService.update(integration);
        IntegrationDTO integrationUpdateResultDTO =  mapper.map(integration, IntegrationDTO.class);

        IntegrationInfo integrationInfo = integrationService.retrieveInfoByIntegration(integration);
        integrationUpdateResultDTO.setConnected(integrationInfo.isConnected());

        return integrationUpdateResultDTO;
    }

}
