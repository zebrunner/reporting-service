package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.properties.MailTemplateProps;
import com.zebrunner.reporting.domain.properties.S3ClientProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

@Configuration
@EnableConfigurationProperties({S3ClientProps.class, MailTemplateProps.class})
public class S3ClientConfiguration {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(S3ClientProps props) {
        return () -> AwsBasicCredentials.create(props.getAccessKeyId(), props.getSecret());
    }

    @Bean
    public S3Client s3client(S3ClientProps props, AwsCredentialsProvider awsCredentialsProvider) {
        S3ClientBuilder clientBuilder = S3Client.builder()
                                                .region(Region.of(props.getRegion()))
                                                .credentialsProvider(awsCredentialsProvider);

        String endpoint = props.getEndpoint();
        if (endpoint != null) {
            clientBuilder.endpointOverride(URI.create(endpoint));
        }

        return clientBuilder.build();
    }

}
