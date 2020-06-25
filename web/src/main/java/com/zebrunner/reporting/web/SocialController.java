package com.zebrunner.reporting.web;

import com.zebrunner.reporting.service.TwitterService;
import com.zebrunner.reporting.web.documented.SocialDocumentedController;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin
@RequestMapping(path = "v1/social", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twitter.enabled", havingValue = "true")
public class SocialController implements SocialDocumentedController {

    private final TwitterService twitterService;

    @GetMapping("/tweets")
    @Override
    public List<Tweet> getZebrunnerTweets(@RequestParam(value = "pageSize", defaultValue = "25") int pageSize) {
        return twitterService.getZebrunnerTweets(pageSize);
    }

}
