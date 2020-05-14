package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.TestRunResultDTO;
import com.zebrunner.reporting.domain.dto.TestSuiteType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;

@Api("Test suites operations")
public interface TestSuiteDocumentedController {

    @ApiOperation(
            value = "Creates a test suite",
            notes = "Returns the created test suite",
            nickname = "createTestSuite",
            httpMethod = "POST",
            response = TestSuiteType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "testSuite", paramType = "body", dataType = "TestSuiteType", required = true, value = "The test suite to create")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the created test suite", response = TestSuiteType.class)
    })
    TestSuiteType createTestSuite(TestSuiteType testSuite);

    @ApiOperation(
            value = "Retrieves test run results(simplified test run objects) by the test suite id",
            notes = "Returns found test run results",
            nickname = "getTestSuiteResultsById",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataTypeClass = Long.class, required = true, value = "The test suite id"),
            @ApiImplicitParam(name = "limit", paramType = "query", dataTypeClass = Long.class, required = true, value = "Number of suite latest test run results to be returned")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found test run results", response = Map.class)
    })
    List<TestRunResultDTO> getTestSuiteResultsById(Long id, Long limit);
}
