package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestSessionSearchCriteria;
import com.zebrunner.reporting.domain.dto.testsession.SearchParameter;
import com.zebrunner.reporting.domain.dto.testsession.TokenDTO;
import com.zebrunner.reporting.domain.entity.TestSession;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Test session API")
public interface TestSessionDocumentedController {

    @ApiOperation(
            value = "Retrieves test session by its session id",
            notes = "Returns the found test session",
            nickname = "getById",
            httpMethod = "GET",
            response = TestSession.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "sessionId", paramType = "path", dataType = "string", required = true, value = "The session id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found test sessions", response = TestSession.class)
    })
    TestSession getBySessionId(String sessionId);

    @ApiOperation(
            value = "Searches for test sessions by specified criteria",
            notes = "Returns the found test sessions",
            nickname = "search",
            httpMethod = "GET",
            response = SearchResult.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "criteria", paramType = "body", dataType = "TestSessionSearchCriteria", required = true, value = "Search criteria to search")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the found test sessions", response = SearchResult.class)
    })
    SearchResult<TestSession> search(TestSessionSearchCriteria criteria);

    @ApiOperation(
            value = "Retrieves unique search parameters set that can be applied to search criteria",
            notes = "Returns collected parameters",
            nickname = "getSearchParameters",
            httpMethod = "GET",
            response = SearchParameter.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns collected parameters", response = SearchParameter.class)
    })
    SearchParameter getSearchParameters();

    @ApiOperation(
            value = "Generates new Zebrunner Hub token",
            notes = "Returns new token",
            nickname = "updateZbrHubToken",
            httpMethod = "GET",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "integrationId", paramType = "query", dataTypeClass = Long.class, required = true, value = "Integration id for which token must be regenerated")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns generated token", response = String.class)

    })
    TokenDTO refreshToken(Long integrationId);

}
