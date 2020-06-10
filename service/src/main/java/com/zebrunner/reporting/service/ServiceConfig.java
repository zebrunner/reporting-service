package com.zebrunner.reporting.service;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.StsClient;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableCaching(proxyTargetClass = true)
@EnableConfigurationProperties(S3Properties.class)
public class ServiceConfig {

    private static final String BASENAME_LOCATION = "classpath:i18n/services/messages";

    @Bean
    public PasswordEncryptor passwordEncryptor() {
        return new BasicPasswordEncryptor();
    }

    @Bean
    public MessageSource serviceMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(BASENAME_LOCATION);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(S3Properties props) {
        return () -> AwsBasicCredentials.create(props.getAccessKeyId(), props.getSecret());
    }

    @Bean
    public S3Client s3Client(S3Properties props, AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
                       .credentialsProvider(credentialsProvider)
                       .region(Region.of(props.getRegion()))
                       .build();
    }

    @Bean
    public StsClient stsClient(AwsCredentialsProvider credentialsProvider) {
        return StsClient.builder().credentialsProvider(credentialsProvider).build();
    }

}
