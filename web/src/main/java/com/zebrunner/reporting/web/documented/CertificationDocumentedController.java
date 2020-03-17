package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.CertificationType;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Certification API")
public interface CertificationDocumentedController {

    @ApiOperation(
            value = "Searches for certification info by upstream job details",
            notes = "Returns certification information (screenshots, platforms, correlation id), or null if an elasticsearch client is not initialized",
            nickname = "getCertificationDetails",
            httpMethod = "GET",
            response = CertificationType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "upstreamJobId", paramType = "query", dataTypeClass = Long.class, required = true, value = "The upstream job id"),
            @ApiImplicitParam(name = "upstreamJobBuildNumber", paramType = "query", dataTypeClass = Integer.class, required = true, value = "The build number of the upstream job")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns certification details", response = CertificationType.class),
            @ApiResponse(code = 404, message = "Indicates that the test run cannot be found during the details collection process", response = ErrorResponse.class)
    })
    CertificationType getCertificationDetails(Long upstreamJobId, Integer upstreamJobBuildNumber);

}
