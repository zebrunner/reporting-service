package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.EmailType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api("File utils API")
public interface FileUtilDocumentedController {

    @ApiOperation(
            value = "Sends a file by email",
            notes = "Sends a file using information about recipients from the part named ‘email’",
            nickname = "sendImageByEmail",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "email", paramType = "path", dataType = "MultipartFile", required = true, value = "The multipart file part named 'email'"),
            @ApiImplicitParam(name = "file", paramType = "path", dataType = "MultipartFile", required = true, value = "The multipart file part named 'file'")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Indicates that the email was sent")
    })
    void sendImageByEmail(MultipartFile file, EmailType email) throws IOException;

}
