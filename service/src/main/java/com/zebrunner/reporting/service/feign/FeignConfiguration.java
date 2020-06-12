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
    return new Request.Options(5000, TimeUnit.MILLISECONDS,
            30000, TimeUnit.MILLISECONDS, false);
  }

  @Bean
  public Retryer retryer() {
    return new Retryer.Default(1000, 8000, 3);
  }

}