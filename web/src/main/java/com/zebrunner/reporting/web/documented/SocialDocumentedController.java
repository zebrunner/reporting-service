package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.social.twitter.api.Tweet;

import java.util.List;

@Api("Social API")
public interface SocialDocumentedController {

    @ApiOperation(
            value = "Returns user twitter timeline",
            notes = "Returns user twitter timeline by user name",
            nickname = "getUserTimeline",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", paramType = "path", dataType = "String", required = true, value = "User screen name"),
            @ApiImplicitParam(name = "pageSize", paramType = "path", dataType = "int", required = false, value = "Amount of tweets")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns user tweets", response = List.class),
            @ApiResponse(code = 400, message = "Indicates that the userName not specified", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Indicates that twitter user not found", response = ErrorResponse.class)
    })
    List<Tweet> getUserTimeline(String userName, int pageSize);
}
