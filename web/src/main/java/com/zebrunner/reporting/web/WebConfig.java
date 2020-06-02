package com.zebrunner.reporting.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zebrunner.reporting.web.util.dozer.NullSafeDozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@EnableAsync
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class WebConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

    private static final String BASENAME_LOCATION = "classpath:i18n/messages";

    private final boolean multitenant;

    public WebConfig(ObjectMapper objectMapper, @Value("${service.multitenant}") boolean multitenant) {
        this.multitenant = multitenant;

        objectMapper(objectMapper);
    }

    // hypothetical fix for a peculiar communication scenario of SockJS.
    // sometimes SockJS is not able to establish a native websocket connection.
    // in this case, it uses some http-based alternatives.
    // one of these alternatives expects application/javascript content to be returned.
    // generally, spring internals handles client's expectations.
    // but if client disconnects before the connection has been fully established,
    // Spring throws an error which is handled by the api exception handler.
    // the result of this handling cannot be converted to application/javascript, which leads to useless exceptions in logs.
    // hypothetically, this mappingJackson2HttpMessageConverter should help gracefully omit the error.
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

        List<MediaType> mediaTypes = new ArrayList<>(jsonConverter.getSupportedMediaTypes());
        mediaTypes.add(new MediaType("application", "javascript"));
        jsonConverter.setSupportedMediaTypes(mediaTypes);

        return jsonConverter;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(BASENAME_LOCATION);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        if (!multitenant) {
            // add extra resource handler to serve local resources - single host deployment only
            registry.addResourceHandler("/assets/**")
                    .addResourceLocations("file:/opt/assets/");
        }
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

    @Bean
    public NullSafeDozerBeanMapper mapper(ResourceLoader resourceLoader) {
        List<String> mappingsFileNames = retrieveDozerMappingsUrls(resourceLoader, "classpath*:dozer/**/*.xml");
        NullSafeDozerBeanMapper beanMapper = new NullSafeDozerBeanMapper();
        beanMapper.setMappingFiles(mappingsFileNames);
        return beanMapper;
    }

    private List<String> retrieveDozerMappingsUrls(ResourceLoader resourceLoader, String mappingsLocationPattern) {
        List<String> mappingsFileNames = null;
        try {
            Resource[] dozerMappings = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                                                           .getResources(mappingsLocationPattern);
            mappingsFileNames = Arrays.stream(dozerMappings).map(resource -> {
                String mappingPath = null;
                try {
                    mappingPath = resource.getURL().toString();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return mappingPath;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return mappingsFileNames;
    }

    @Bean
    public Docket api(@Value("${service.docs-enabled:false}") boolean docsEnabled) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("reporting-service-api")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zebrunner.reporting.web"))
                .paths(PathSelectors.any())
                .build()
                .enable(docsEnabled)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Reporting service REST API")
                .description("Reporting service REST API documentation")
                .termsOfServiceUrl("https://github.com/zebrunner/reporting-service")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/zebrunner/reporting-service/blob/master/LICENSE")
                .version("2.0")
                .build();
    }

    /**
     * Registers placeholder configurer to resolve properties
     * Order is required, `cause  there is at least one placeholder configurer in servlet context by default.
     * Order is necessary to resolve their conflicts
     *
     * @return a created placeholder configurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        placeholderConfigurer.setOrder(Integer.MIN_VALUE);
        return placeholderConfigurer;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    // TODO: 3/24/20 got rid of the block if jackson.serialization.write-dates-as-timestamps will be false
    public ObjectMapper objectMapper(ObjectMapper objectMapper) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(OffsetDateTime.class, new JsonSerializer<>() {
            @Override
            public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime));
            }
        });
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

}
