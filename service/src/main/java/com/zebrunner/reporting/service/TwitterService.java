package com.zebrunner.reporting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twitter.enabled", havingValue = "true")
public class TwitterService {

    private final Twitter twitter;

    public List<Tweet> getUserTimeline(String userName, int pageSize) {
        return twitter.timelineOperations().getUserTimeline(userName, pageSize);
    }
}
