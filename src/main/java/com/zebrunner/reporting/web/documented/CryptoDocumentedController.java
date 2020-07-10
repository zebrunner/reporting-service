package com.zebrunner.reporting.web.documented;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Security API")
public interface CryptoDocumentedController {

    @ApiOperation(
            value = "Generates a new crypto key",
            notes = "Generates a crypto key and re-encrypts all encrypted app values according to this new key",
            nickname = "regenerateCryptoKey",
            httpMethod = "PUT"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Indicates that the crypto key was regenerated successfully, and all settings were re-encrypted")
    })
    void regenerateCryptoKey();

}
