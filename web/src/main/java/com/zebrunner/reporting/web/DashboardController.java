package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Attribute;
import com.zebrunner.reporting.domain.db.Dashboard;
import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.domain.db.Widget;
import com.zebrunner.reporting.domain.dto.DashboardType;
import com.zebrunner.reporting.domain.dto.EmailType;
import com.zebrunner.reporting.service.DashboardService;
import com.zebrunner.reporting.service.WidgetTemplateService;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.util.EmailUtils;
import com.zebrunner.reporting.web.documented.DashboardDocumentedController;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RequestMapping(path = "api/dashboards", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class DashboardController extends AbstractController implements DashboardDocumentedController {

    private static final String ERR_MSG_ILLEGAL_DASHBOARD_ACCESS_BY_ID = "Cannot access requested dashboard by id '%d'";
    private static final String ERR_MSG_ILLEGAL_DASHBOARD_ACCESS_BY_TITLE = "Cannot access requested dashboard by title '%s'";

    private final DashboardService dashboardService;
    private final WidgetTemplateService widgetTemplateService;
    private final Mapper mapper;

    public DashboardController(DashboardService dashboardService, WidgetTemplateService widgetTemplateService, Mapper mapper) {
        this.dashboardService = dashboardService;
        this.widgetTemplateService = widgetTemplateService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS') and ((hasPermission('VIEW_HIDDEN_DASHBOARDS') and #dashboardType.hidden) or !#dashboardType.hidden)")
    @PostMapping()
    @Override
    public DashboardType createDashboard(@RequestBody @Valid DashboardType dashboardType) {
        Dashboard dashboard = mapper.map(dashboardType, Dashboard.class);
        dashboard = dashboardService.createDashboard(dashboard);
        return mapper.map(dashboard, DashboardType.class);
    }

    @GetMapping()
    @Override
    public List<DashboardType> getAllDashboards(@RequestParam(value = "hidden", required = false) boolean hidden) {
        List<Dashboard> dashboards;
        if (!hidden && hasPermission(Permission.Name.VIEW_HIDDEN_DASHBOARDS)) {
            dashboards = dashboardService.retrieveAll();
        } else {
            dashboards = dashboardService.retrieveByVisibility(false);
        }

        return dashboards.stream()
                         .map(dashboard -> mapper.map(dashboard, DashboardType.class))
                         .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Override
    public DashboardType getDashboardById(@PathVariable("id") long id) {
        Dashboard dashboard = dashboardService.getDashboardById(id);
        boolean rejectResource = !dashboard.isSystem() && dashboard.isHidden() && !hasPermission(Permission.Name.VIEW_HIDDEN_DASHBOARDS);
        if (rejectResource) {
            throw new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.DASHBOARD_NOT_FOUND, String.format(ERR_MSG_ILLEGAL_DASHBOARD_ACCESS_BY_ID, id));
        }
        dashboard.getWidgets().forEach(widget -> widgetTemplateService.clearRedundantParamsValues(widget.getWidgetTemplate()));
        return mapper.map(dashboard, DashboardType.class);
    }

    @GetMapping("/title")
    @Override
    public DashboardType getDashboardByTitle(@RequestParam(name = "title", required = false) String title) {
        Dashboard dashboard = dashboardService.retrieveByTitle(title);
        boolean rejectResource = !dashboard.isSystem() && dashboard.isHidden() && !hasPermission(Permission.Name.VIEW_HIDDEN_DASHBOARDS);
        if (rejectResource) {
            throw new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.DASHBOARD_NOT_FOUND, String.format(ERR_MSG_ILLEGAL_DASHBOARD_ACCESS_BY_TITLE, title));
        }
        return mapper.map(dashboard, DashboardType.class);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @DeleteMapping("/{id}")
    @Override
    public void deleteDashboard(@PathVariable("id") long id) {
        dashboardService.removeById(id);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping()
    @Override
    public DashboardType updateDashboard(@Valid @RequestBody DashboardType dashboardType) {
        Dashboard dashboard = mapper.map(dashboardType, Dashboard.class);
        dashboard = dashboardService.update(dashboard);
        return mapper.map(dashboard, DashboardType.class);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping("/order")
    @Override
    public Map<Long, Integer> updateDashboardsOrder(@RequestBody Map<Long, Integer> order) {
        return dashboardService.updateDashboardsOrder(order);
    }

    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PostMapping("/{dashboardId}/widgets")
    @Override
    public Widget addDashboardWidget(@PathVariable("dashboardId") long dashboardId, @RequestBody Widget widget) {
        return dashboardService.addDashboardWidget(dashboardId, widget);
    }

    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @DeleteMapping("/{dashboardId}/widgets/{widgetId}")
    @Override
    public void deleteDashboardWidget(@PathVariable("dashboardId") long dashboardId, @PathVariable("widgetId") long widgetId) {
        dashboardService.removeDashboardWidget(dashboardId, widgetId);
    }

    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PutMapping("/{dashboardId}/widgets")
    @Override
    public Widget updateDashboardWidget(@PathVariable("dashboardId") long dashboardId, @RequestBody Widget widget) {
        return dashboardService.updateDashboardWidget(dashboardId, widget);
    }

    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PutMapping("/{dashboardId}/widgets/all")
    @Override
    public List<Widget> updateDashboardWidgets(@PathVariable("dashboardId") long dashboardId, @RequestBody List<Widget> widgets) {
        return dashboardService.updateDashboardWidgets(dashboardId, widgets);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PostMapping("/{dashboardId}/attributes")
    @Override
    public List<Attribute> createDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @RequestBody Attribute attribute) {
        dashboardService.createDashboardAttribute(dashboardId, attribute);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PostMapping("/{dashboardId}/attributes/batch")
    @Override
    public List<Attribute> createDashboardAttributes(@PathVariable("dashboardId") long dashboardId, @RequestBody List<Attribute> attributes) {
        dashboardService.createDashboardAttributes(dashboardId, attributes);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @PutMapping("/{dashboardId}/attributes")
    @Override
    public List<Attribute> updateDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @RequestBody Attribute attribute) {
        dashboardService.updateAttribute(attribute);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

    @PreAuthorize("hasPermission('MODIFY_DASHBOARDS')")
    @DeleteMapping("/{dashboardId}/attributes/{id}")
    @Override
    public List<Attribute> deleteDashboardAttribute(@PathVariable("dashboardId") long dashboardId, @PathVariable("id") long id) {
        dashboardService.removeByAttributeById(id);
        return dashboardService.retrieveAttributesByDashboardId(dashboardId);
    }

    @PostMapping("/email")
    @Override
    @SneakyThrows
    public void sendByEmail(@RequestPart("file") MultipartFile file, @RequestPart("email") EmailType email) {
        String fileExtension = String.format(".%s", FilenameUtils.getExtension(file.getOriginalFilename()));
        File attachment = File.createTempFile(UUID.randomUUID().toString(), fileExtension);
        file.transferTo(attachment);
        String[] toEmails = EmailUtils.obtainRecipients(email.getRecipients());
        dashboardService.sendByEmail(email.getSubject(), email.getText(), List.of(attachment), toEmails);
    }

}
