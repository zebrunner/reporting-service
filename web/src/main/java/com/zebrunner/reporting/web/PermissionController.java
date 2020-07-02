package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.service.PermissionService;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("Permissions API")
@CrossOrigin
@RequestMapping(path = "api/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves all permissions", nickname = "getAllPermissions", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_USER_GROUPS')")
    @GetMapping()
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

}
