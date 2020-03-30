package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.dto.JobDTO;
import com.zebrunner.reporting.domain.dto.JobUrlType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@Api("Jobs API")
public interface JobDocumentedController {

    @ApiOperation(
            value = "Creates or updates a job",
            notes = "Creates a job if it does not exist. Otherwise, updates it",
            nickname = "createJob",
            httpMethod = "POST",
            response = JobDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "jobDTO", paramType = "body", dataType = "JobDTO", required = true, value = "The job to create or update")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created or updated job", response = JobDTO.class)
    })
    JobDTO createJob(JobDTO jobDTO);

    @ApiOperation(
            value = "Creates a job using Jenkins job URL",
            notes = "Returns the created job",
            nickname = "createJobByUrl",
            httpMethod = "POST",
            response = JobDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "jobUrl", paramType = "body", dataType = "JobUrlType", required = true, value = "The job URL to create a new job")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created job", response = JobDTO.class)
    })
    JobDTO createJobByUrl(JobUrlType jobUrl);

    @ApiOperation(
            value = "Retrieves all jobs",
            notes = "Returns found jobs",
            nickname = "getAllJobs",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found jobs", response = List.class)
    })
    List<Job> getAllJobs();
}
