package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.UserPreference;
import com.zebrunner.reporting.domain.dto.UserPreferenceDTO;
import com.zebrunner.reporting.service.DashboardService;
import com.zebrunner.reporting.service.UserPreferenceService;
import com.zebrunner.reporting.service.UserService;
import com.zebrunner.reporting.web.documented.UserDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RequestMapping(path = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UserController extends AbstractController implements UserDocumentedController {

    private final UserService userService;
    private final DashboardService dashboardService;
    private final UserPreferenceService userPreferenceService;
    private final Mapper mapper;

    public UserController(UserService userService, DashboardService dashboardService,
                          UserPreferenceService userPreferenceService, Mapper mapper) {
        this.userService = userService;
        this.dashboardService = dashboardService;
        this.userPreferenceService = userPreferenceService;
        this.mapper = mapper;
    }

    @GetMapping("/preferences")
    @Override
    public List<UserPreference> getDefaultUserPreferences() {
        User user = userService.getDefault();
        return userPreferenceService.getAllUserPreferences(user.getId());
    }

    @Override
    @GetMapping("{userId}/preferences")
    public List<UserPreference> getUserPreference(@PathVariable("userId") long userId) {
        return userPreferenceService.getAllUserPreferences(userId);
    }

    @Override
    @GetMapping("{userId}/preferences/extended")
    public Map<String, Object> getUserPreferenceWithDashboards(@PathVariable("userId") long userId) {
        Map<String, Object> extendedUserPreferences = new HashMap<>(5);

        dashboardService.setDefaultDashboard(extendedUserPreferences, userId, "", "defaultDashboardId");
        dashboardService.setDefaultDashboard(extendedUserPreferences, userId, "User Performance", "performanceDashboardId");
        dashboardService.setDefaultDashboard(extendedUserPreferences, userId, "Personal", "personalDashboardId");
        dashboardService.setDefaultDashboard(extendedUserPreferences, userId, "Stability", "stabilityDashboardId");
        extendedUserPreferences.put("preferences", userPreferenceService.getAllUserPreferences(userId));

        return extendedUserPreferences;
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
