package com.zebrunner.reporting.web.documented;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Health check API")
public interface ApplicationHealthDocumentedController {

    @ApiOperation(
            value = "Checks application health",
            notes = "Returns common information about the application",
            nickname = "getStatus",
            httpMethod = "GET",
            response = String.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns common information about the application", response = String.class)
    })
    String getStatus();

}
