package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.BinaryObject;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Api("Assets API")
public interface AssetsDocumentedController {

    @ApiOperation(
            value = "Stores provided asset",
            notes = "Stores provided asset. Depending on asset type validation applied accordingly",
            nickname = "saveAsset",
            httpMethod = "POST",
            response = Map.class
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Returns stored asset key (relative to origin)", response = Map.class),
            @ApiResponse(code = 400, message = "Returns error describing why asset is not valid", response = ErrorResponse.class)
    })
    Map<String, String> saveAsset(BinaryObject.Type type, MultipartFile file);

    @ApiOperation(
            value = "Deletes stored asset by its key",
            notes = "Deletes stored asset by its key",
            nickname = "deleteAsset",
            httpMethod = "DELETE"
    )
    @ApiResponses({
            @ApiResponse(code = 204, message = "On successfully deleted asset")
    })
    void deleteAsset(String key);

}
