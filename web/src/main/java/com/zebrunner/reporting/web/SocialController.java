package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Invitation;
import com.zebrunner.reporting.domain.dto.auth.InvitationListType;
import com.zebrunner.reporting.domain.dto.auth.InvitationType;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchCriteria;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.service.InvitationService;
import com.zebrunner.reporting.service.TestService;
import com.zebrunner.reporting.service.TwitterService;
import com.zebrunner.reporting.web.documented.InvitationDocumentedController;
import com.zebrunner.reporting.web.documented.SocialDocumentedController;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin
@RequestMapping(path = "api/social", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twitter.enabled", havingValue = "true")
public class SocialController extends AbstractController implements SocialDocumentedController {

    private final TwitterService twitterService;

    @GetMapping("/twitter/timeline")
    @Override
    public List<Tweet> getUserTimeline(@RequestParam("userName") String userName, @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        return twitterService.getUserTimeline(userName, pageSize);
    }
}
