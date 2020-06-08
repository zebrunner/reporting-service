package com.zebrunner.reporting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
@ConditionalOnProperty(name = "twitter.enabled", havingValue = "true")
public class TwitterConfig {

    private final String consumerKey;
    private final String consumerSecret;

    public TwitterConfig(@Value("${twitter.consumer-key}") String consumerKey,
                         @Value("${twitter.consumer-secret}") String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    @Bean
    public TwitterTemplate getTwitterTemplate() {
        return new TwitterTemplate(consumerKey, consumerSecret);
    }
}
