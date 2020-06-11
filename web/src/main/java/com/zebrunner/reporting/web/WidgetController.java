package com.zebrunner.reporting.web;

import com.zebrunner.reporting.persistence.utils.SQLAdapter;
import com.zebrunner.reporting.domain.db.Attribute;
import com.zebrunner.reporting.domain.db.Widget;
import com.zebrunner.reporting.domain.db.WidgetTemplate;
import com.zebrunner.reporting.domain.dto.QueryParametersDTO;
import com.zebrunner.reporting.domain.dto.widget.WidgetTemplateDTO;
import com.zebrunner.reporting.domain.dto.widget.WidgetDTO;
import com.zebrunner.reporting.service.WidgetService;
import com.zebrunner.reporting.service.WidgetTemplateService;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api("Widgets API")
@RequestMapping(path = "api/widgets", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class WidgetController extends AbstractController {

    private final WidgetService widgetService;
    private final WidgetTemplateService widgetTemplateService;
    private final Mapper mapper;

    public WidgetController(WidgetService widgetService,
                            WidgetTemplateService widgetTemplateService,
                            Mapper mapper) {
        this.widgetService = widgetService;
        this.widgetTemplateService = widgetTemplateService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create widget", nickname = "createWidget", httpMethod = "POST", response = Widget.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PostMapping()
    public WidgetDTO createWidget(@RequestBody @Valid WidgetDTO widgetDTO) {

        Widget widget = mapper.map(widgetDTO, Widget.class);
        return mapper.map(widgetService.createWidget(widget), WidgetDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get widget", nickname = "getWidget", httpMethod = "GET", response = Widget.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/{id}")
    public Widget getWidget(@PathVariable("id") long id) {
        return widgetService.getWidgetById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete widget", nickname = "deleteWidget", httpMethod = "DELETE")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @DeleteMapping("/{id}")
    public void deleteWidget(@PathVariable("id") long id) {
        widgetService.deleteWidgetById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update widget", nickname = "updateWidget", httpMethod = "PUT", response = Widget.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_WIDGETS')")
    @PutMapping()
    public Widget updateWidget(@RequestBody WidgetDTO widgetDTO) {
        Widget widget = mapper.map(widgetDTO, Widget.class);
        return widgetService.updateWidget(widget);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all widgets", nickname = "getAllWidgets", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping()
    public List<WidgetDTO> getAllWidgets() {
        return widgetService.getAllWidgets()
                            .stream()
                            .map(widget -> {
                                widgetTemplateService.clearRedundantParamsValues(widget.getWidgetTemplate());
                                return mapper.map(widget, WidgetDTO.class);
                            }).collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all widget templates", nickname = "getAllWidgetTemplates", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/templates")
    public List<WidgetTemplateDTO> getAllWidgetTemplates() {
        List<WidgetTemplate> widgetTemplates = widgetTemplateService.getWidgetTemplates();
        return widgetTemplates.stream()
                              .map(widgetTemplate -> mapper.map(widgetTemplate, WidgetTemplateDTO.class))
                              .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Prepare widget template data by id", nickname = "prepareWidgetTemplateById", httpMethod = "GET", response = WidgetTemplateDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/templates/{id}/prepare")
    public WidgetTemplateDTO prepareWidgetTemplate(@PathVariable("id") Long id) {
        return mapper.map(widgetTemplateService.prepareWidgetTemplateById(id), WidgetTemplateDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Execute SQL template", nickname = "executeSQLTemplate", httpMethod = "POST", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PostMapping("/templates/sql")
    public List<Map<String, Object>> executeSQL(@RequestBody @Valid QueryParametersDTO queryParametersDTO) {
        Long templateId = queryParametersDTO.getTemplateId();
        Map<String, Object> queryParams = queryParametersDTO.getParamsConfig();
        return widgetService.getQueryResults(queryParams, templateId, Long.valueOf(getPrincipalId()), getPrincipalName());
    }

}
