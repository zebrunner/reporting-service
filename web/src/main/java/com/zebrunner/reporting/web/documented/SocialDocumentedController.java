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
            value = "Returns Zebrunner tweets",
            notes = "Returns Zebrunner tweets",
            nickname = "getUserTweets",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", paramType = "query", dataType = "int", value = "Amount of tweets")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns user tweets", response = List.class),
            @ApiResponse(code = 404, message = "Indicates that twitter user not found", response = ErrorResponse.class)
    })
    List<Tweet> getZebrunnerTweets(int pageSize);

}
