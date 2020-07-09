package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.db.Setting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@Api("Settings API")
public interface SettingDocumentedController {

    @ApiOperation(
            value = "Retrieves default integration settings by integration type name",
            notes = "Will be deprecated soon. Returns integration settings and decrypts encrypted settings. " +
                    "Works with 'ELASTICSEARCH'(for reporting-ui) and 'ZEBRUNNER'(for reporting-ui) types only",
            nickname = "getSettingsByTool",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "typeName", paramType = "path", dataType = "string", required = true, value = "Integration type name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found integration settings", response = List.class)
    })
    List<Setting> getSettingsByTool(String typeName);

}
