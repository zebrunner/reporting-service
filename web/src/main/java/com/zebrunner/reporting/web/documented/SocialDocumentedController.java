package com.zebrunner.reporting.web.documented;

import com.zebrunner.reporting.domain.db.Invitation;
import com.zebrunner.reporting.domain.dto.auth.InvitationListType;
import com.zebrunner.reporting.domain.dto.auth.InvitationType;
import com.zebrunner.reporting.domain.dto.errors.ErrorResponse;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchCriteria;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import io.swagger.annotations.*;
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
            @ApiResponse(code = 400, message = "Indicates that the userName not specified", response = ErrorResponse.class)
    })
    List<Tweet> getUserTimeline(String userName, int pageSize);
}
