package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.db.UserPreference;
import com.zebrunner.reporting.domain.dto.UserPreferenceDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;

@Api("Users API")
public interface UserDocumentedController {

    @ApiOperation(
            value = "Retrieves default system user preferences",
            notes = "Returns found preferences",
            nickname = "getDefaultUserPreferences",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found preferences", response = List.class)
    })
    List<UserPreference> getDefaultUserPreferences();

    @ApiOperation(
            value = "Returns user preferences by id",
            notes = "Returns user preferences by id",
            nickname = "getUserPreference",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "The user id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns attached preferences", response = UserPreference.class, responseContainer = "List")
    })
    List<UserPreference> getUserPreference(long userId);

    @ApiOperation(
            value = "Returns user preferences by id",
            notes = "An extended user preferences additionally include user default dashboards ids",
            nickname = "getUserPreference",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "The user id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns attached preferences and user default dashboards ids", response = Map.class)
    })
    Map<String, Object> getUserPreferenceWithDashboards(long userId);

    @ApiOperation(
            value = "Creates user preferences",
            notes = "Returns created preferences",
            nickname = "createUserPreference",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "The user id"),
            @ApiImplicitParam(name = "preferences", paramType = "body", dataType = "array", required = true, value = "The preferences to attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns attached preferences", response = List.class)
    })
    List<UserPreference> createUserPreference(long userId, List<UserPreference> preferences);

    @ApiOperation(
            value = "Updates single user preference",
            notes = "Returns updated integration",
            nickname = "createUserPreference",
            httpMethod = "PUT",
            response = UserPreferenceDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The path reference id"),
            @ApiImplicitParam(name = "name", paramType = "query", dataType = "string", required = true, value = "User preference name"),
            @ApiImplicitParam(name = "value", paramType = "query", dataType = "string", required = true, value = "User preference value")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated user preference", response = UserPreferenceDTO.class)
    })
    UserPreferenceDTO createUserPreference(long userId, UserPreference.Name name, String value);

    @ApiOperation(
            value = "Resets current user preferences to default",
            notes = "Returns default preferences",
            nickname = "resetUserPreferencesToDefault",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns default preferences", response = List.class)
    })
    List<UserPreference> resetUserPreferencesToDefault();

    @ApiOperation(
            value = "Deletes user preferences",
            notes = "Deletes user preferences by the user id",
            nickname = "deleteUserPreferences",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The user id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User preferences were deleted successfully")
    })
    void deleteUserPreferences(long userId);

}
