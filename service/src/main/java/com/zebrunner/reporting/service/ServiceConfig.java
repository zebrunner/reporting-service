package com.zebrunner.reporting.service;

import feign.RequestInterceptor;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableCaching(proxyTargetClass = true)
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

}
