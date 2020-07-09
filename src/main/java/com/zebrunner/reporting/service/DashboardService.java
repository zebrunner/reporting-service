package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.DashboardMapper;
import com.zebrunner.reporting.domain.db.Attribute;
import com.zebrunner.reporting.domain.db.Dashboard;
import com.zebrunner.reporting.domain.db.Widget;
import com.zebrunner.reporting.domain.dto.user.UserDTO;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.DASHBOARD_ATTRIBUTE_CAN_NOT_BE_CREATED;
import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.DASHBOARD_CAN_NOT_BE_CREATED;
import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.DASHBOARD_CAN_NOT_BE_UPDATED;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.DASHBOARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final String ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND = "Dashboard with id %s can not be found";
    private static final String ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND_BY_ATTRIBUTE_ID = "Dashboard which contains an attribute with id '%d' can not be found";
    private static final String ERR_MSG_DASHBOARD_ALREADY_EXISTS = "Dashboard with such title already exists";
    private static final String ERR_MSG_UNEDITABLE_DASHBOARD_CANT_BE_ALTERED = "Uneditable dashboard can not be updated or deleted";
    private static final String ERR_MSG_DASHBOARD_ATTRIBUTES_ALREADY_IN_USE = "Attributes with keys (%s) already in use for dashboard with id '%d'";
    private static final String ERR_MSG_DASHBOARD_ATTRIBUTE_ALREADY_IN_USE = "Attribute with key '%s' already in use for dashboard with id '%d'";
    private static final String ERR_MSG_CANNOT_PERSIST_ATTRIBUTE_TO_SYSTEM_DASHBOARD = "Can not create or update attribute";

    private final DashboardMapper dashboardMapper;
    private final UserPreferenceService userPreferenceService;
    private final EmailService emailService;

    @Transactional
    public Dashboard createDashboard(Dashboard dashboard) {
        if (retrieveByTitle(dashboard.getTitle()) != null) {
            throw new IllegalOperationException(DASHBOARD_CAN_NOT_BE_CREATED, ERR_MSG_DASHBOARD_ALREADY_EXISTS);
        }
        dashboard.setEditable(true);
        dashboardMapper.createDashboard(dashboard);
        return dashboard;
    }

    @Transactional(readOnly = true)
    public Dashboard getDashboardById(Long id) {
        Dashboard dashboard = dashboardMapper.getDashboardById(id);
        if (dashboard == null) {
            throw new ResourceNotFoundException(DASHBOARD_NOT_FOUND, ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND, id);
        }
        return dashboard;
    }

    @Transactional(readOnly = true)
    public List<Dashboard> retrieveAll() {
        return dashboardMapper.getAllDashboards();
    }

    @Transactional(readOnly = true)
    public List<Dashboard> retrieveByVisibility(boolean hidden) {
        return dashboardMapper.getDashboardsByHidden(hidden);
    }

    @Transactional(readOnly = true)
    public Dashboard retrieveByTitle(String title) {
        return dashboardMapper.getDashboardByTitle(title);
    }

    @Transactional(readOnly = true)
    public Dashboard retrieveDefaultForUser(Long userId) {
        return dashboardMapper.getDefaultDashboardByUserId(userId);
    }

    @Transactional
    public Dashboard update(Dashboard updatedDashboard) {
        Dashboard dashboard = getDashboardById(updatedDashboard.getId());
        // only editable dashboard can be modified, throw exception otherwise
        if (dashboard.isEditable()) {
            updatedDashboard.setEditable(true);
            dashboardMapper.updateDashboard(updatedDashboard);

            // if title changed - update user preferences as well
            String currentTitle = dashboard.getTitle();
            String newTitle = updatedDashboard.getTitle();
            if (!currentTitle.equals(newTitle)) {
                userPreferenceService.updateDefaultDashboardPreference(currentTitle, newTitle);
            }
            return updatedDashboard;
        } else {
            throw new IllegalOperationException(DASHBOARD_CAN_NOT_BE_UPDATED, ERR_MSG_UNEDITABLE_DASHBOARD_CANT_BE_ALTERED);
        }
    }

    @Transactional
    public Map<Long, Integer> updateDashboardsOrder(Map<Long, Integer> dashboardsPositions) {
        dashboardsPositions.forEach(dashboardMapper::updateDashboardOrder);
        return dashboardsPositions;
    }

    @Transactional
    public void removeById(Long id) {
        Dashboard dashboard = getDashboardById(id);
        // only editable dashboard can be deleted, throw exception otherwise
        if (dashboard.isEditable()) {
            // reset dashboard preference first, then delete
            userPreferenceService.resetDefaultDashboardPreference(dashboard.getTitle());
            dashboardMapper.deleteDashboardById(id);
        } else {
            throw new IllegalOperationException(DASHBOARD_CAN_NOT_BE_UPDATED, ERR_MSG_UNEDITABLE_DASHBOARD_CANT_BE_ALTERED);
        }
    }

    @Transactional
    public Widget addDashboardWidget(Long dashboardId, Widget widget) {
        dashboardMapper.addDashboardWidget(dashboardId, widget);
        return widget;
    }

    @Transactional
    public Widget updateDashboardWidget(Long dashboardId, Widget widget) {
        dashboardMapper.updateDashboardWidget(dashboardId, widget);
        return widget;
    }

    @Transactional
    public List<Widget> updateDashboardWidgets(Long dashboardId, List<Widget> widgets) {
        Dashboard dashboard = getDashboardById(dashboardId);
        if (dashboard.isEditable()) {
            return widgets.stream()
                          .map(widget -> updateDashboardWidget(dashboardId, widget))
                          .collect(Collectors.toList());
        } else {
            throw new IllegalOperationException(DASHBOARD_CAN_NOT_BE_UPDATED, ERR_MSG_UNEDITABLE_DASHBOARD_CANT_BE_ALTERED);
        }
    }

    @Transactional
    public void removeDashboardWidget(Long dashboardId, Long widgetId) {
        dashboardMapper.deleteDashboardWidget(dashboardId, widgetId);
    }

    @Transactional
    public List<Attribute> retrieveAttributesByDashboardId(Long dashboardId) {
        return dashboardMapper.getAttributesByDashboardId(dashboardId);
    }

    @Transactional
    public Attribute createDashboardAttribute(Long dashboardId, Attribute attribute) {
        Dashboard dashboard = getDashboardById(dashboardId);
        if (dashboard.isEditable()) {
            Attribute usedAttribute = retrieveAttributesByDashboardId(dashboardId).stream()
                                                                                  .filter(attr -> attr.getKey().equals(attribute.getKey()))
                                                                                  .findAny()
                                                                                  .orElse(null);
            if (usedAttribute != null) {
                String message = String.format(ERR_MSG_DASHBOARD_ATTRIBUTE_ALREADY_IN_USE, usedAttribute.getKey(), dashboardId);
                throw new IllegalOperationException(DASHBOARD_ATTRIBUTE_CAN_NOT_BE_CREATED, message);
            }
            dashboardMapper.createDashboardAttribute(dashboardId, attribute);
        } else {
            throw new IllegalOperationException(DASHBOARD_ATTRIBUTE_CAN_NOT_BE_CREATED, ERR_MSG_CANNOT_PERSIST_ATTRIBUTE_TO_SYSTEM_DASHBOARD);
        }
        return attribute;
    }

    @Transactional
    public List<Attribute> createDashboardAttributes(Long dashboardId, List<Attribute> attributes) {
        Dashboard dashboard = getDashboardById(dashboardId);
        if (dashboard.isEditable()) {
            List<String> dashboardAttributeKeys = retrieveAttributesByDashboardId(dashboardId).stream()
                                                                                              .map(Attribute::getKey)
                                                                                              .collect(Collectors.toList());
            List<String> usedKeys = attributes.stream()
                                              .filter(attribute -> dashboardAttributeKeys.contains(attribute.getKey()))
                                              .map(Attribute::getKey)
                                              .collect(Collectors.toList());
            if (!usedKeys.isEmpty()) {
                String keysAsString = String.join(", ", usedKeys);
                String message = usedKeys.size() > 1 ?
                        String.format(ERR_MSG_DASHBOARD_ATTRIBUTES_ALREADY_IN_USE, keysAsString, dashboardId) :
                        String.format(ERR_MSG_DASHBOARD_ATTRIBUTE_ALREADY_IN_USE, usedKeys.get(0), dashboardId);
                throw new IllegalOperationException(DASHBOARD_ATTRIBUTE_CAN_NOT_BE_CREATED, message);
            }
            dashboardMapper.createDashboardAttributes(dashboardId, attributes);
        } else {
            throw new IllegalOperationException(DASHBOARD_ATTRIBUTE_CAN_NOT_BE_CREATED, ERR_MSG_CANNOT_PERSIST_ATTRIBUTE_TO_SYSTEM_DASHBOARD, dashboardId);
        }
        return attributes;
    }

    @Transactional(readOnly = true)
    public Dashboard retrieveByAttributeId(Long attributeId) {
        Dashboard dashboard = dashboardMapper.getByAttributeId(attributeId);
        if (dashboard == null) {
            throw new ResourceNotFoundException(DASHBOARD_NOT_FOUND, ERR_MSG_DASHBOARD_CAN_NOT_BE_FOUND_BY_ATTRIBUTE_ID, attributeId);
        }
        return dashboard;
    }

    @Transactional
    public Attribute updateAttribute(Attribute attribute) {
        Dashboard dashboard = retrieveByAttributeId(attribute.getId());
        if (dashboard.isEditable()) {
            dashboardMapper.updateAttribute(attribute);
        } else {
            throw new IllegalOperationException(DASHBOARD_ATTRIBUTE_CAN_NOT_BE_CREATED, ERR_MSG_CANNOT_PERSIST_ATTRIBUTE_TO_SYSTEM_DASHBOARD);
        }
        return attribute;
    }

    @Transactional
    public void removeByAttributeById(long attributeId) {
        Dashboard dashboard = retrieveByAttributeId(attributeId);
        if (dashboard.isEditable()) {
            dashboardMapper.deleteDashboardAttributeById(attributeId);
        } else {
            throw new IllegalOperationException(DASHBOARD_ATTRIBUTE_CAN_NOT_BE_CREATED, ERR_MSG_CANNOT_PERSIST_ATTRIBUTE_TO_SYSTEM_DASHBOARD);
        }
    }

    @Transactional
    public void setDefaultDashboard(Map<String, Object> extendedUserProfile, Long userId, String title, String key) {
        Dashboard dashboard = "defaultDashboardId".equals(key)
                ? retrieveDefaultForUser(userId)
                : retrieveByTitle(title);
        if (dashboard == null) {
            extendedUserProfile.put(key, null);
        } else {
            extendedUserProfile.put(key, dashboard.getId());
        }
    }

    public void sendByEmail(String subject, String body, List<File> attachments, String... toEmails) {
        emailService.sendDashboardEmail(subject, body, attachments, toEmails);
    }

}
