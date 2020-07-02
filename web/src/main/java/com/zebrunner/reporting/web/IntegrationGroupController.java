package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.dto.integration.IntegrationGroupDTO;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.web.documented.IntegrationGroupDocumentedController;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RequestMapping(path = "api/integration-groups", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class IntegrationGroupController extends AbstractController implements IntegrationGroupDocumentedController {

    private final IntegrationGroupService integrationGroupService;
    private final Mapper mapper;

    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS')")
    @GetMapping()
    @Override
    public List<IntegrationGroupDTO> getAll() {
        return integrationGroupService.retrieveAll().stream()
                                 .map(integration -> mapper.map(integration, IntegrationGroupDTO.class))
                                 .collect(Collectors.toList());
    }

}
