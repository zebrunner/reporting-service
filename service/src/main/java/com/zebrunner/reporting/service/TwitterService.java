package com.zebrunner.reporting.service;

import com.zebrunner.reporting.service.exception.ForbiddenOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twitter.enabled", havingValue = "true")
public class TwitterService {

    public static final String ZEBRUNNER_NEWS = "zebrunner_news";

    private static final int MAX_PAGE_SIZE = 50;

    private final Twitter twitter;

    /**
     * Read user latest tweets
     * @param userName - twitter user name
     * @param pageSize - amount of latest tweets to read
     * @return user tweets
     */
    public List<Tweet> getUserTweets(String userName, int pageSize) {
        // Verify allowed user names
        if(!Arrays.asList(ZEBRUNNER_NEWS).contains(userName)) {
            throw new ForbiddenOperationException("Unsupported user to get tweets: " + userName);
        }
        // Verify max page size
        if(pageSize > MAX_PAGE_SIZE) {
            throw new ForbiddenOperationException("Max 50 tweets allowed, actual: " +  pageSize) ;
        }
        return twitter.timelineOperations().getUserTimeline(userName, pageSize);
    }
}
