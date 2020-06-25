package com.zebrunner.reporting.service;

import com.zebrunner.reporting.service.exception.ForbiddenOperationException;
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

    public static final String ZEBRUNNER_ACCOUNT_NAME = "zebrunner_news";
    private static final int MAX_PAGE_SIZE = 50;

    private final Twitter twitter;

    /**
     * Read user latest tweets
     *
     * @param pageSize amount of latest tweets to read
     * @return user tweets
     */
    public List<Tweet> getZebrunnerTweets(int pageSize) {
        if (pageSize > MAX_PAGE_SIZE) {
            throw new ForbiddenOperationException("Max 50 tweets allowed, actual: " + pageSize);
        }
        return twitter.timelineOperations().getUserTimeline(ZEBRUNNER_ACCOUNT_NAME, pageSize);
    }

}
