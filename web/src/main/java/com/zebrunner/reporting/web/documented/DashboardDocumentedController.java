package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.db.Attribute;
import com.zebrunner.reporting.domain.db.Widget;
import com.zebrunner.reporting.domain.dto.DashboardType;
import com.zebrunner.reporting.domain.dto.EmailType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api("Dashboards API")
public interface DashboardDocumentedController {

    @ApiOperation(
            value = "Creates a dashboard",
            notes = "Returns the created editable dashboard",
            nickname = "createDashboard",
            httpMethod = "POST",
            response = DashboardType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardType", paramType = "body", dataType = "DashboardType", required = true, value = "The dashboard to create"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created dashboard", response = DashboardType.class),
            @ApiResponse(code = 400, message = "Indicates that a dashboard with the same name already exists", response = ResponseEntity.class)
    })
    DashboardType createDashboard(DashboardType dashboardType);

    @ApiOperation(
            value = "Retrieves all dashboards by visibility (according to visibility parameter/principle)",
            notes = "Returns found dashboards. To retrieve hidden dashboards user must to have separate permission",
            nickname = "getAllDashboards",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "hidden", paramType = "query", dataTypeClass = Boolean.class, value = "A flag to retrieve hidden dashboards"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found dashboards. To retrieve hidden dashboards, user needs separate permission", response = List.class)
    })
    List<DashboardType> getAllDashboards(boolean hidden);

    @ApiOperation(
            value = "Retrieves a dashboard by its id",
            notes = "Returns the found dashboard if it exists",
            nickname = "getDashboardById",
            httpMethod = "GET",
            response = DashboardType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The dashboard id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found dashboard", response = DashboardType.class),
            @ApiResponse(code = 404, message = "Indicates that the dashboard is not found", response = ResponseEntity.class)
    })
    DashboardType getDashboardById(long id);

    @ApiOperation(
            value = "Retrieves a dashboard by its title",
            notes = "Returns the found dashboard, or null if the dashboard does not exist",
            nickname = "getDashboardByTitle",
            httpMethod = "GET",
            response = DashboardType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "title", paramType = "query", dataType = "string", value = "The dashboard title")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found dashboard", response = DashboardType.class),
            @ApiResponse(code = 404, message = "Indicates that the dashboard is not found", response = ResponseEntity.class)
    })
    DashboardType getDashboardByTitle(String title);

    @ApiOperation(
            value = "Deletes a dashboard by its id",
            notes = "Deletes a dashboard if it is editable",
            nickname = "deleteDashboard",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The dashboard id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The dashboard was deleted successfully"),
            @ApiResponse(code = 400, message = "Indicates that the dashboard is not editable", response = ResponseEntity.class)
    })
    void deleteDashboard(long id);

    @ApiOperation(
            value = "Updates a dashboard",
            notes = "Returns the updated dashboard",
            nickname = "updateDashboard",
            httpMethod = "PUT",
            response = DashboardType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardType", paramType = "body", dataType = "DashboardType", required = true, value = "The dashboard to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated dashboard", response = DashboardType.class),
            @ApiResponse(code = 404, message = "Indicates that the dashboard is not editable", response = ResponseEntity.class)
    })
    DashboardType updateDashboard(DashboardType dashboardType);

    @ApiOperation(
            value = "Updates the dashboard order",
            notes = "Returns dashboard ids in an updated order ",
            nickname = "updateDashboardsOrder",
            httpMethod = "PUT",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "order", paramType = "body", dataType = "object", required = true, value = "New dashboards order to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns dashboard ids in an updated order", response = Map.class)
    })
    Map<Long, Integer> updateDashboardsOrder(Map<Long, Integer> order);

    @ApiOperation(
            value = "Adds a widget to a dashboard by the dashboard id",
            notes = "The widget will appear on a specified dashboard",
            nickname = "addDashboardWidget",
            httpMethod = "POST",
            response = Widget.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The dashboard id"),
            @ApiImplicitParam(name = "widget", paramType = "body", dataType = "Widget", required = true, value = "The widget to attach")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the added widget", response = Widget.class)
    })
    Widget addDashboardWidget(long dashboardId, Widget widget);

    @ApiOperation(
            value = "Deletes a widget from a dashboard",
            notes = "The widget will be removed from a specified dashboard only",
            nickname = "deleteDashboardWidget",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The dashboard id"),
            @ApiImplicitParam(name = "widgetId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The widget id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The widget was removed from the dashboard successfully")
    })
    void deleteDashboardWidget(long dashboardId, long widgetId);

    @ApiOperation(
            value = "Updates a widget in a dashboard by its id",
            notes = "Returns the updated widget",
            nickname = "updateDashboardWidget",
            httpMethod = "PUT",
            response = Widget.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The dashboard id"),
            @ApiImplicitParam(name = "widget", paramType = "body", dataType = "Widget", required = true, value = "The widget to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated widget if it exists in the dashboard", response = Widget.class)
    })
    Widget updateDashboardWidget(long dashboardId, Widget widget);

    @ApiOperation(
            value = "Updates a batch of widgets in a dashboard by its id",
            notes = "Returns updated widgets",
            nickname = "updateDashboardWidgets",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataTypeClass = Long.class, required = true, value = "The dashboard id"),
            @ApiImplicitParam(name = "widgets", paramType = "body", dataType = "array", required = true, value = "Widgets to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns updated widgets", response = List.class)
    })
    List<Widget> updateDashboardWidgets(long dashboardId, List<Widget> widgets);

    @ApiOperation(
            value = "Creates a dashboard attribute",
            notes = "The dashboard attribute will be used to obtain data for creating a widget",
            nickname = "createDashboardAttribute",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataTypeClass = Long.class, required = true, value = "Dashboard id which has an attribute"),
            @ApiImplicitParam(name = "attribute", paramType = "body", dataType = "Attribute", required = true, value = "Attribute to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all dashboard attributes including the created attribute", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that a dashboard attribute with the same keys already exists or is not editable", response = ResponseEntity.class)
    })
    List<Attribute> createDashboardAttribute(long dashboardId, Attribute attribute);

    @ApiOperation(
            value = "Creates a batch of dashboard attributes",
            notes = "Dashboard attributes will be used to obtain data for creating a widget",
            nickname = "createDashboardAttributes",
            httpMethod = "POST",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataTypeClass = Long.class, required = true, value = "Dashboard id which has an attributes"),
            @ApiImplicitParam(name = "attributes", paramType = "body", dataType = "List", required = true, value = "Attributes to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all dashboard attributes including created attributes", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that a dashboard attributes with the same keys already exist or is not editable", response = ResponseEntity.class)
    })
    List<Attribute> createDashboardAttributes(long dashboardId, List<Attribute> attributes);

    @ApiOperation(
            value = "Updates the properties of an existing dashboard attribute",
            notes = "The updated properties will be used to obtain data for creating a widget",
            nickname = "updateDashboardAttribute",
            httpMethod = "PUT",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataTypeClass = Long.class, required = true, value = "Dashboard id which has an attribute"),
            @ApiImplicitParam(name = "attribute", paramType = "body", dataType = "Attribute", required = true, value = "Attribute to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all dashboard attributes including the updated attribute", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that a dashboard attributes is not editable", response = ResponseEntity.class)
    })
    List<Attribute> updateDashboardAttribute(long dashboardId, Attribute attribute);

    @ApiOperation(
            value = "Deletes an attribute from a dashboard",
            notes = "The deleted attribute will not be used to obtain data for creating a widget",
            nickname = "deleteDashboardAttribute",
            httpMethod = "DELETE",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "dashboardId", paramType = "path", dataTypeClass = Long.class, required = true, value = "Dashboard id which has an attribute"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "Attribute id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns all dashboard attributes excluding the deleted attribute", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that a dashboard attributes is not editable", response = ResponseEntity.class)
    })
    List<Attribute> deleteDashboardAttribute(long dashboardId, long id);

    @ApiOperation(
            value = "Sends a dashboard by email",
            notes = "Sends a dashboard using information about recipients from the part named ‘email’",
            nickname = "sendByEmail",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "email", paramType = "path", dataType = "MultipartFile", required = true, value = "The multipart file part named 'email'"),
            @ApiImplicitParam(name = "file", paramType = "path", dataType = "MultipartFile", required = true, value = "The multipart file part named 'file'")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Indicates that the dashboard email was sent")
    })
    void sendByEmail(MultipartFile file, EmailType email);

}
