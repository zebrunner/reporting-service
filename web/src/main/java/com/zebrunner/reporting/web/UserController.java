package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.UserSearchCriteria;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.UserPreference;
import com.zebrunner.reporting.domain.dto.UserPreferenceDTO;
import com.zebrunner.reporting.domain.dto.user.ChangePasswordDTO;
import com.zebrunner.reporting.domain.dto.user.UserDTO;
import com.zebrunner.reporting.service.DashboardService;
import com.zebrunner.reporting.service.UserPreferenceService;
import com.zebrunner.reporting.service.UserService;
import com.zebrunner.reporting.web.documented.UserDocumentedController;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RequestMapping(path = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class UserController extends AbstractController implements UserDocumentedController {

    private final UserService userService;
    private final DashboardService dashboardService;
    private final UserPreferenceService userPreferenceService;
    private final Mapper mapper;

    @GetMapping("/profile")
    @Override
    public UserDTO getUserProfile(@RequestParam(value = "username", required = false) String username) {
        User user = StringUtils.isEmpty(username) ? userService.getNotNullUserById(getPrincipalId())
                                                  : userService.getNotNullUserByUsername(username);
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        userDTO.setRoles(user.getRoles());
        userDTO.setPreferences(user.getPreferences());
        userDTO.setPermissions(user.getPermissions());
        return userDTO;
    }

    @GetMapping("/profile/extended")
    @Override
    public Map<String, Object> getExtendedUserProfile() {
        Map<String, Object> extendedUserProfile = new HashMap<>();
        User user = userService.getUserById(getPrincipalId());
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        userDTO.setRoles(user.getRoles());
        userDTO.setPreferences(user.getPreferences());
        userDTO.setPermissions(user.getPermissions());
        extendedUserProfile.put("user", userDTO);
        dashboardService.setDefaultDashboard(extendedUserProfile, "", "defaultDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "User Performance", "performanceDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "Personal", "personalDashboardId");
        dashboardService.setDefaultDashboard(extendedUserProfile, "Stability", "stabilityDashboardId");
        return extendedUserProfile;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_WIDGETS')")
    @Override
    public UserDTO create(@Valid @RequestBody UserDTO userDTO) {
        User user = mapper.map(userDTO, User.class);

        user = userService.create(user, null);

        userDTO = mapper.map(user, UserDTO.class);
        return userDTO;
    }

    @PutMapping("/{id}")
    @Override
    public UserDTO update(@Valid @RequestBody UserDTO userDTO, @PathVariable("id") Long id) {
        checkCurrentUserAccess(id);

        User user = mapper.map(userDTO, User.class);
        user.setId(id);

        boolean fullUpdate = isAdmin() && hasPermission(Permission.Name.MODIFY_USERS);
        if (fullUpdate) {
            user = userService.update(user);
        } else {
            user = userService.updateUserProfile(user);
        }

        userDTO = mapper.map(user, UserDTO.class);
        userDTO.setRoles(userDTO.getRoles());
        userDTO.setPreferences(userDTO.getPreferences());
        return userDTO;
    }

    @PutMapping("/password")
    @Override
    public void updateUserPassword(@Valid @RequestBody ChangePasswordDTO password) {
        checkCurrentUserAccess(password.getUserId());
        boolean forceUpdate = isAdmin() && password.getOldPassword() == null;
        userService.updateUserPassword(password.getUserId(), password.getOldPassword(), password.getPassword(), forceUpdate);
    }

    @PreAuthorize("#isPublic or (hasRole('ROLE_ADMIN') and hasAnyPermission('VIEW_USERS', 'MODIFY_USERS'))")
    @PostMapping("/search")
    @Override
    public SearchResult<User> searchUsers(
            @Valid @RequestBody UserSearchCriteria searchCriteria,
            @RequestParam(value = "public", required = false) boolean isPublic
    ) {
        return userService.searchUsers(searchCriteria, isPublic);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USERS')")
    @PutMapping("/status")
    @Override
    public UserDTO updateStatus(@RequestBody @Valid UserDTO userDTO) {
        User user = mapper.map(userDTO, User.class);
        user = userService.updateStatus(user);
        return mapper.map(user, UserDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @PutMapping("/group/{id}")
    @Override
    public User addUserToGroup(@RequestBody User user, @PathVariable("id") long id) {
        return userService.addUserToGroup(user, id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_USER_GROUPS')")
    @DeleteMapping("/{userId}/group/{groupId}")
    @Override
    public void deleteUserFromGroup(@PathVariable("groupId") long groupId, @PathVariable("userId") long userId) {
        userService.deleteUserFromGroup(groupId, userId);
    }

    @GetMapping("/preferences")
    @Override
    public List<UserPreference> getDefaultUserPreferences() {
        User user = userService.getDefaultUser();
        return userPreferenceService.getAllUserPreferences(user.getId());
    }

    @PutMapping(value = "{userId}/preferences")
    @Override
    public List<UserPreference> createUserPreference(@PathVariable("userId") long userId, @RequestBody List<UserPreference> preferences) {
        preferences.forEach(userPreferenceService::createOrUpdateUserPreference);
        return userPreferenceService.getAllUserPreferences(userId);
    }

    @PutMapping(value = "{userId}/preference")
    @Override
    public UserPreferenceDTO createUserPreference(@PathVariable("userId") long userId,
                                                  @RequestParam UserPreference.Name name,
                                                  @RequestParam String value) {
        userPreferenceService.createOrUpdateUserPreference(new UserPreference(name, value, userId));
        UserPreference userPreference = userPreferenceService.getUserPreferenceByNameAndUserId(name.name(), userId);
        return mapper.map(userPreference, UserPreferenceDTO.class);
    }

    @PutMapping("/preferences/default")
    @Override
    public List<UserPreference> resetUserPreferencesToDefault() {
        return userPreferenceService.resetUserPreferencesToDefault(getPrincipalId());
    }

    @DeleteMapping("/{userId}/preferences")
    @Override
    public void deleteUserPreferences(@PathVariable("userId") long userId) {
        userPreferenceService.deleteUserPreferencesByUserId(userId);
    }

}
