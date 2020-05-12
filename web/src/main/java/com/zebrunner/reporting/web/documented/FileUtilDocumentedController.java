package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.EmailType;
import com.zebrunner.reporting.domain.dto.aws.FileUploadType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api("File utils API")
public interface FileUtilDocumentedController {

    @ApiOperation(
            value = "Uploads a file to a needed location using the file type",
            notes = "Returns the URL of an uploaded file",
            nickname = "uploadFile",
            httpMethod = "POST",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "type", paramType = "header", dataType = "string", required = true, value = "The file type (USERS, COMMON, VIDEOS or SCREENSHOTS)"),
            @ApiImplicitParam(name = "file", paramType = "path", dataType = "MultipartFile", required = true, value = "The file to upload")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the URL of the uploaded file", response = String.class)
    })
    String uploadFile(FileUploadType.Type type, MultipartFile file) throws IOException;

    @ApiOperation(
            value = "Deletes a file from needed location using the file type",
            notes = "Stores file in local filesystem or in S3",
            nickname = "deleteFile",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "type", paramType = "header", dataType = "string", required = true, value = "The file type (USERS, COMMON, VIDEOS or SCREENSHOTS)"),
            @ApiImplicitParam(name = "key", paramType = "query", dataType = "string", required = true, value = "The file url")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "File was deleted successfully"),
            @ApiResponse(code = 400, message = "Indicates that a file cannot be deleted", response = ResponseEntity.class)
    })
    void deleteFile(FileUploadType.Type type, String key);

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

    @ApiOperation(
            value = "Downloads a file from a/the public server folder by its name",
            notes = "Commonly used to download APK files",
            nickname = "downloadFile",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "filename", paramType = "path", dataType = "string", required = true, value = "The name of the file to download")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Indicates that the file was downloaded successfully")
    })
    void downloadFile(HttpServletResponse response, String filename) throws IOException;

    @ApiOperation(
            value = "Checks whether a file is present in a public server folder by its name",
            notes = "Commonly used to check existing APK files",
            nickname = "checkFilePresence",
            httpMethod = "GET",
            response = Boolean.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "filename", paramType = "query", dataType = "string", required = true, value = "The name of the file to check")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns true if the file was found by its name", response = Boolean.class)
    })
    boolean checkFilePresence(String filename);

}
