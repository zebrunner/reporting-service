package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.ProjectDTO;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@Api("Projects API")
public interface ProjectDocumentedController {

    @ApiOperation(
            value = "Creates a project",
            notes = "Returns the created project",
            nickname = "createProject",
            httpMethod = "POST",
            response = ProjectDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "project", paramType = "body", dataType = "ProjectDTO", required = true, value = "The project to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created project", response = ProjectDTO.class)
    })
    ProjectDTO createProject(ProjectDTO project);

    @ApiOperation(
            value = "Deletes a project by its id",
            notes = "Reassigns all artifacts to the specified project id for reassign or to default project if project id for reassign is null",
            nickname = "deleteProject",
            httpMethod = "DELETE"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The project id"),
            @ApiImplicitParam(name = "reassignToId", paramType = "query", dataTypeClass = Long.class, value = "The id of the project to reassign all artifacts while deleting")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "The project was deleted successfully"),
            @ApiResponse(code = 404, message = "Indicates that the default project does not exist", response = ErrorResponse.class)
    })
    void deleteProject(long id, Long reassignToId);

    @ApiOperation(
            value = "Updates a project",
            notes = "Returns the updated project",
            nickname = "updateProject",
            httpMethod = "PUT",
            response = ProjectDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "project", paramType = "body", dataType = "ProjectDTO", required = true, value = "The project to update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated project", response = ProjectDTO.class)
    })
    ProjectDTO updateProject(ProjectDTO project);

    @ApiOperation(
            value = "Retrieves all projects",
            notes = "Returns found projects",
            nickname = "getAllProjects",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found projects", response = List.class)
    })
    List<ProjectDTO> getAllProjects();

    @ApiOperation(
            value = "Retrieves a project by its name",
            notes = "Returns the found project",
            nickname = "getProjectByName",
            httpMethod = "GET",
            response = ProjectDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "name", paramType = "path", dataType = "string", required = true, value = "The project name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found project", response = ProjectDTO.class),
            @ApiResponse(code = 404, message = "Indicates that the project does not exist", response = ErrorResponse.class)
    })
    ProjectDTO getProjectByName(String name);

}
