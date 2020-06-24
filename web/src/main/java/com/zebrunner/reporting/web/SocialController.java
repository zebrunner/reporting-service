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
@RequestMapping(path = "v1/social", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twitter.enabled", havingValue = "true")
public class SocialController extends AbstractController implements SocialDocumentedController {

    private final TwitterService twitterService;

    @GetMapping("/tweets")
    @Override
    public List<Tweet> getUserTweets(@RequestParam(value = "userName", defaultValue = TwitterService.ZEBRUNNER_NEWS) String userName, @RequestParam(value = "pageSize", defaultValue = "25") int pageSize) {
        return twitterService.getUserTweets(userName, pageSize);
    }
}
