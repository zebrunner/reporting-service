package com.zebrunner.reporting.domain.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "s3")
public final class S3ClientProps {

    private String endpoint;
    private String accessKeyId;
    private String secret;
    private String region;
    private String bucket;

}
