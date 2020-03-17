package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import com.zebrunner.reporting.domain.entity.integration.IntegrationInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;

@Api("Integrations info API")
public interface IntegrationInfoDocumentedController {

    @ApiOperation(
            value = "Retrieves all integration connections info grouped by integration types",
            notes = "Returns all core integration attributes and groups them by integration type names",
            nickname = "getIntegrationsInfo",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found integrations", response = Map.class)
    })
    Map<String, Map<String, List<IntegrationInfo>>> getIntegrationsInfo();

    @ApiOperation(
            value = "Retrieves integration connections info by id",
            notes = "Returns the core attributes of the integration by its id and the group it belongs to",
            nickname = "getIntegrationsInfoById",
            httpMethod = "GET",
            response = IntegrationInfo.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The integration id"),
            @ApiImplicitParam(name = "groupName", paramType = "query", dataType = "string", required = true, value = "The integration group name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found integration", response = IntegrationInfo.class),
            @ApiResponse(code = 404, message = "Indicates that the integration cannot be found, and its information cannot be obtained", response = ErrorResponse.class)
    })
    IntegrationInfo getIntegrationsInfoById(Long id, String groupName);

}
