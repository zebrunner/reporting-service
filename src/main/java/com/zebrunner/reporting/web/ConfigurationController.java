package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Project;
import com.zebrunner.reporting.service.project.ProjectService;
import com.zebrunner.reporting.service.util.URLResolver;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api("Configuration API")
@CrossOrigin
@RequestMapping(path = "api/config", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class ConfigurationController extends AbstractController {

    private final ProjectService projectService;
    private final URLResolver urlResolver;

    @Setter(onMethod = @__(@Value("${service.version}")))
    private String serviceVersion;

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves the version value/number", nickname = "getVersion", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/version")
    public Map<String, Object> getVersion() {
        return Map.of("service", serviceVersion, "service_url", urlResolver.buildWebserviceUrl());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves all projects", nickname = "getAllProjects", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

}
