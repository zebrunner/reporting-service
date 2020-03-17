package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;
import com.zebrunner.reporting.domain.dto.integration.IntegrationDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Api("Integrations API")
public interface IntegrationDocumentedController {

    @ApiOperation(
            value = "Creates an integration and links it to a specified type by its id",
            notes = "Returns the created integration",
            nickname = "create",
            httpMethod = "POST",
            response = IntegrationDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "integrationDTO", paramType = "body", dataType = "IntegrationDTO", required = true, value = "The integration to create"),
            @ApiImplicitParam(name = "integrationTypeId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The integration type id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created integration", response = IntegrationDTO.class),
            @ApiResponse(code = 400, message = "Indicates that obligatory integration fields contain wrong values, or the integration is malformed", response = ResponseEntity.class)
    })
    IntegrationDTO create(IntegrationDTO integrationDTO, Long integrationTypeId);

    @ApiOperation(
            value = "Retrieves integrations",
            notes = "Retrieves integrations by their group id or group name. If no query parameters found, retrieves all existing integrations",
            nickname = "getAll",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "groupId", paramType = "query", dataTypeClass = Long.class, value = "The integration group id"),
            @ApiImplicitParam(name = "groupName", paramType = "query", dataType = "string", value = "The integration group name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found integrations", response = List.class)
    })
    List<IntegrationDTO> getAll(Long groupId, String groupName);

    @ApiOperation(
            value = "Creates Amazon temporary credentials",
            notes = "Returns created temporary credentials from Amazon integration",
            nickname = "getAmazonTemporaryCredentials",
            httpMethod = "GET",
            response = SessionCredentials.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns Amazon session credentials, or null if the operation is not possible", response = SessionCredentials.class)
    })
    SessionCredentials getAmazonTemporaryCredentials();

    @ApiOperation(
            value = "Updates an integration by its id",
            notes = "Returns the updated integration",
            nickname = "update",
            httpMethod = "PUT",
            response = IntegrationDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "integrationDTO", paramType = "body", dataType = "IntegrationDTO", required = true, value = "The integration to update"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The integration id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated integration", response = IntegrationDTO.class)
    })
    IntegrationDTO update(IntegrationDTO integrationDTO, Long id);

}
