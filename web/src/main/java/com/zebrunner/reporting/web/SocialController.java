package com.zebrunner.reporting.web;

import com.zebrunner.reporting.service.TwitterService;
import com.zebrunner.reporting.service.exception.ForbiddenOperationException;
import com.zebrunner.reporting.web.documented.SocialDocumentedController;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@CrossOrigin
@RequestMapping(path = "api/social", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twitter.enabled", havingValue = "true")
public class SocialController extends AbstractController implements SocialDocumentedController {

    private static final String ZEBRUNNER_NEWS = "zebrunner_news";

    private final TwitterService twitterService;

    @GetMapping("/twitter/timeline")
    @Override
    public List<Tweet> getUserTimeline(@RequestParam(value = "userName", defaultValue = ZEBRUNNER_NEWS) String userName, @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        // Verify allowed user names
        if(!Arrays.asList(ZEBRUNNER_NEWS).contains(userName)) {
            throw new ForbiddenOperationException("Unsupported user for twitter timeline: " + userName);
        }
        return twitterService.getUserTimeline(userName, pageSize);
    }
}
