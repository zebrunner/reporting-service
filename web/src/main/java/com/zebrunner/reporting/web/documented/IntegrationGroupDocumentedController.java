package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.integration.IntegrationGroupDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@Api("Integration groups API")
public interface IntegrationGroupDocumentedController {

    @ApiOperation(
            value = "Retrieves all integration groups",
            notes = "Returns found integration groups",
            nickname = "getAll",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found integration groups", response = List.class)
    })
    List<IntegrationGroupDTO> getAll();

}
