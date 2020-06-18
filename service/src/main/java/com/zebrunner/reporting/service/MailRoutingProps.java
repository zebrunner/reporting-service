package com.zebrunner.reporting.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "routing.mail")
public final class MailRoutingProps {

    private String exchangeName;
    private String queueName;
    private String key;

}
