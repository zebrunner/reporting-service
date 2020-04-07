package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.UserSearchCriteria;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.UserPreference;
import com.zebrunner.reporting.domain.dto.UserPreferenceDTO;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import com.zebrunner.reporting.domain.dto.user.ChangePasswordDTO;
import com.zebrunner.reporting.domain.dto.user.UserDTO;
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
            value = "Retrieves a user profile",
            notes = "Retrieves a user profile by the username or auth token if the username is not specified",
            nickname = "getUserProfile",
            httpMethod = "GET",
            response = UserDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "username", paramType = "query", dataType = "string", value = "The user name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found user", response = UserDTO.class),
            @ApiResponse(code = 404, message = "Indicates that the user does not exist", response = ErrorResponse.class)
    })
    UserDTO getUserProfile(String username);

    @ApiOperation(
            value = "Retrieves an extended user profile",
            notes = "An extended user profile includes user info, user default dashboards ids",
            nickname = "getExtendedUserProfile",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns collected extended user profile", response = Map.class)
    })
    Map<String, Object> getExtendedUserProfile();

    @ApiOperation(
            value = "Updates a user profile",
            notes = "Returns the updated user profile",
            nickname = "updateUserProfile",
            httpMethod = "PUT",
            response = UserDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userDTO", paramType = "body", dataType = "UserDTO", required = true, value = "The user to update"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The user id"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated user profile", response = UserDTO.class),
            @ApiResponse(code = 404, message = "Indicates that user does not exist", response = ErrorResponse.class)
    })
    UserDTO updateUserProfile(UserDTO userDTO, Long id);

    @ApiOperation(
            value = "Updates a user password",
            notes = "Only the profile owner and admin have access to update user password",
            nickname = "updateUserPassword",
            httpMethod = "PUT"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "password", paramType = "body", dataType = "ChangePasswordDTO", required = true, value = "The reset password object")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User password was updated successfully"),
            @ApiResponse(code = 400, message = "Indicates that it’s not the profile owner or admin who tries to update the password", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that user does not exist", response = ErrorResponse.class)
    })
    void updateUserPassword(ChangePasswordDTO password);

    @ApiOperation(
            value = "Searches for users by specified criteria",
            notes = "Returns found users",
            nickname = "searchUsers",
            httpMethod = "POST",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "searchCriteria", paramType = "body", dataType = "UserSearchCriteria", required = true, value = "Search criteria"),
            @ApiImplicitParam(name = "isPublic", paramType = "query", dataType = "boolean", value = "Indicates that the search will include only user public info")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found users", response = SearchResult.class)
    })
    SearchResult<User> searchUsers(UserSearchCriteria searchCriteria, boolean isPublic);

    @ApiOperation(
            value = "Updates a user status",
            notes = "Activates or deactivates a user",
            nickname = "updateStatus",
            httpMethod = "PUT",
            response = UserDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "userType", paramType = "body", dataType = "UserType", required = true, value = "The user to activate or deactivate")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the user with an updated status", response = UserDTO.class)
    })
    UserDTO updateStatus(UserDTO userDTO);

    @ApiOperation(
            value = "Adds a user to a group",
            notes = "Adds a user to a group by its id",
            nickname = "addUserToGroup",
            httpMethod = "PUT",
            response = User.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "user", paramType = "body", dataType = "User", required = true, value = "The user to add"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "The id of a group to attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the user with the group they are added to", response = User.class)
    })
    User addUserToGroup(User user, long id);

    @ApiOperation(
            value = "Deletes a user from a group",
            notes = "Detaches a user from a group",
            nickname = "deleteUserFromGroup",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "groupId", paramType = "path", dataType = "number", required = true, value = "The group id"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "number", required = true, value = "The user id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The user was detached successfully")
    })
    void deleteUserFromGroup(long groupId, long userId);

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
