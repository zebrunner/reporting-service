package com.zebrunner.reporting.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Configuration
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
