package com.zebrunner.reporting.service.feign;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfiguration {

  @Bean
  public Request.Options timeoutConfiguration(){
    // Set connectTimeout, readTimeout ant followRedirects flag
    return new Request.Options(5000, TimeUnit.MILLISECONDS, 30000, TimeUnit.MILLISECONDS, false);
  }

  @Bean
  public Retryer retryer() {
    // Set period between attempts, maxPeriod allowed for an attempt and the number of attempts
    return new Retryer.Default(1000, 5000, 3);
  }

}