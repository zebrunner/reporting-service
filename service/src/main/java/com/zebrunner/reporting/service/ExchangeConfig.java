package com.zebrunner.reporting.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@EnableConfigurationProperties(MailRoutingProps.class)
public class ExchangeConfig {

    private final String exchangeName;

    public ExchangeConfig(
            @Value("${spring.rabbitmq.template.exchange}") String exchangeName,
            RabbitTemplate rabbitTemplate
    ) {
        this.exchangeName = exchangeName;
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName, false, true);
    }

    @Bean
    public DirectExchange mailExchange(MailRoutingProps props) {
        return new DirectExchange(props.getExchangeName(), false, false);
    }

    /**
     * Queue with autogenerated name to get rid of duplicate names on different app instances
     *
     * @return setting queue with prefix 'settingsQueue'
     */
    @Bean
    public Queue settingsQueue() {
        return new Queue("settingsQueue", false, false, true);
    }

    @Bean
    public Queue mailQueue(MailRoutingProps props) {
        return new Queue(props.getQueueName(), false);
    }

    /**
     * Queue with autogenerated name to get rid of duplicate names on different app instances
     *
     * @return tenancy queue with prefix 'tenanciesQueue'
     */
    @Bean
    public Queue tenanciesQueue() {
        return new Queue(generateQueueName("tenanciesQueue"), false, false, true);
    }

    @Bean
    public Queue zfrEventsQueue() {
        return new Queue("zfrEventsQueue", false, false, true);
    }

    @Bean
    public Queue zbrEventsQueue() {
        return new Queue("zbrEventsQueue", false, false, true);
    }

    @Bean
    public Queue zfrCallbacksQueue() {
        return new Queue("zfrCallbacksQueue", false, false, true);
    }

    @Bean
    public Binding settingsBinding(DirectExchange exchange, Queue settingsQueue) {
        return BindingBuilder.bind(settingsQueue).to(exchange).with("settings");
    }

    @Bean
    public Binding mailBinding(DirectExchange mailExchange, Queue mailQueue, MailRoutingProps props) {
        return BindingBuilder.bind(mailQueue).to(mailExchange).with(props.getKey());
    }

    @Bean
    public Binding tenanciesBinding(DirectExchange exchange, Queue tenanciesQueue) {
        return BindingBuilder.bind(tenanciesQueue).to(exchange).with("tenancies");
    }

    @Bean
    public Binding zfrEventsBinding(DirectExchange exchange, Queue zfrEventsQueue) {
        return BindingBuilder.bind(zfrEventsQueue).to(exchange).with("zfr_events");
    }

    @Bean
    public Binding zbrEventsBinding(DirectExchange exchange, Queue zbrEventsQueue) {
        return BindingBuilder.bind(zbrEventsQueue).to(exchange).with("zbr_events");
    }

    @Bean
    public Binding zfrCallbacksBinding(DirectExchange exchange, Queue zfrCallbacksQueue) {
        return BindingBuilder.bind(zfrCallbacksQueue).to(exchange).with("zfr_callbacks");
    }

    private String generateQueueName(String prefix) {
        return String.format("%s-%s", prefix, UUID.randomUUID().toString());
    }

}
