package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.auth.AccessTokenDTO;
import com.zebrunner.reporting.domain.dto.auth.AuthTokenDTO;
import com.zebrunner.reporting.domain.dto.auth.CredentialsDTO;
import com.zebrunner.reporting.domain.dto.auth.EmailDTO;
import com.zebrunner.reporting.domain.dto.auth.RefreshTokenDTO;
import com.zebrunner.reporting.domain.dto.auth.TenancyInfoDTO;
import com.zebrunner.reporting.domain.dto.auth.TenantAuth;
import com.zebrunner.reporting.domain.dto.user.PasswordDTO;
import com.zebrunner.reporting.domain.dto.user.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

@Api("Auth API")
public interface AuthDocumentedController {

    @ApiOperation(
            value = "Retrieves information about the tenant",
            notes = "Returns basic tenant information about the tenant, such as the tenant’s name, service URL, etc.",
            nickname = "getTenancyInfo",
            httpMethod = "GET",
            response = TenancyInfoDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns basic information about the tenant", response = TenancyInfoDTO.class)
    })
    TenancyInfoDTO getTenancyInfo();

    @ApiOperation(
            value = "Checks tenant permissions",
            notes = "Checks whether the token has all permissions",
            nickname = "checkPermissions",
            httpMethod = "POST",
            response = ResponseEntity.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantAuth", paramType = "body", dataType = "TenantAuth", required = true, value = "Tenant auth data to check")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Jwt token has all permissions", response = ResponseEntity.class),
            @ApiResponse(code = 403, message = "Jwt token does not have the required permissions", response = ResponseEntity.class)
    })
    ResponseEntity<Void> checkPermissions(TenantAuth tenantAuth);

    @ApiOperation(
            value = "Generates an auth token",
            notes = "Returns the generated auth token that will be used in authenticated API calls",
            nickname = "login",
            httpMethod = "POST",
            response = AuthTokenDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "credentialsDTO", paramType = "body", dataType = "CredentialsDTO", required = true, value = "Credentials for user authentication")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the auth token. If it is a first login, header with name `First-Login` will appear in response.", response = AuthTokenDTO.class),
            @ApiResponse(code = 401, message = "Indicates that the user credentials are invalid", response = ResponseEntity.class)
    })
    AuthTokenDTO login(CredentialsDTO credentialsDTO, HttpServletResponse response);

    @ApiOperation(
            value = "Registers a new user in the application",
            notes = "Creates a user in the application",
            nickname = "signup",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Access-Token", paramType = "header", dataType = "string", required = true, value = "The token that was sent to invite a new user"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "The user information for registration")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Creates a user in the application"),
            @ApiResponse(code = 400, message = "Indicates that the user already exists")
    })
    void signup(String token, UserDTO userDTO);

    @ApiOperation(
            value = "Refreshes an auth token",
            notes = "Returns a refreshed auth token",
            nickname = "refresh",
            httpMethod = "POST",
            response = AuthTokenDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refreshToken", paramType = "body", dataType = "RefreshTokenDTO", required = true, value = "The token to refresh")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns a new auth token", response = AuthTokenDTO.class),
            @ApiResponse(code = 401, message = "Indicates that the token cannot be refreshed", response = ResponseEntity.class)
    })
    AuthTokenDTO refresh(RefreshTokenDTO refreshToken);

    @ApiOperation(
            value = "Sends a reset password email",
            notes = "Generates a reset password token and sends it via email",
            nickname = "sendResetPasswordEmail",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "emailDTO", paramType = "body", dataType = "EmailDTO", required = true, value = "The email to send the reset password token to")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The email was sent successfully"),
            @ApiResponse(code = 404, message = "Indicates that the account does not exist", response = ResponseEntity.class),
    })
    void sendResetPasswordEmail(EmailDTO emailDTO);

    @ApiOperation(
            value = "Checks whether a specified reset password token is valid",
            notes = "Checks whether the account exists and the account source is internal",
            nickname = "checkIfTokenResetIsPossible",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", paramType = "body", dataType = "string", required = true, value = "The reset password token to check")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The reset token is valid, and the reset password operation is possible"),
            @ApiResponse(code = 400, message = "Indicates that the password reset operation is not possible", response = ResponseEntity.class)
    })
    void checkIfTokenResetIsPossible(String token);

    @ApiOperation(
            value = "Resets the old password and changes it with a new one",
            notes = "Checks whether a password reset operation is possible, and changes the password",
            nickname = "resetPassword",
            httpMethod = "PUT"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Access-Token", paramType = "header", required = true, value = "The reset password token"),
            @ApiImplicitParam(name = "passwordDTO", paramType = "body", dataType = "PasswordDTO", required = true, value = "The password to change")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The password was updated successfully"),
            @ApiResponse(code = 400, message = "Indicates that the password reset operation is not possible", response = ResponseEntity.class)
    })
    void resetPassword(String token, PasswordDTO passwordDTO);

    @ApiOperation(
            value = "Generates an API access token",
            notes = "Returns the token that provides access to API",
            nickname = "accessToken",
            httpMethod = "GET",
            response = AccessTokenDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the generated temporary access token", response = AccessTokenDTO.class),
            @ApiResponse(code = 404, message = "Indicates that the user attempting to generate an auth token cannot be recognized", response = ResponseEntity.class)
    })
    AccessTokenDTO accessToken();

}
