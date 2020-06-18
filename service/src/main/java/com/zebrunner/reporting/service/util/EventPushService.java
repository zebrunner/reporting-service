package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.service.MailRoutingProps;
import com.zebrunner.reporting.service.integration.tool.impl.MessageBrokerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EventPushService<T> {

    private static final String SUPPLIER_QUEUE_NAME_HEADER = "SUPPLIER_QUEUE";

    private final RabbitTemplate rabbitTemplate;
    private final MessageBrokerService messageBrokerService;
    private final String exchangeName;
    private final MailRoutingProps mailRoutingProps;

    public EventPushService(RabbitTemplate rabbitTemplate,
                            @Value("${spring.rabbitmq.template.exchange}") String exchangeName,
                            @Lazy MessageBrokerService messageBrokerService,
                            MailRoutingProps mailRoutingProps) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageBrokerService = messageBrokerService;
        this.exchangeName = exchangeName;
        this.mailRoutingProps = mailRoutingProps;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum Routing {

        SETTINGS("settings"),
        ZFR_CALLBACKS("zfr_callbacks"),
        ZBR_EVENTS("zbr_events"),
        TENANCIES("tenancies"),
        MAIL,
        MAIL_INTEGRATION;

        private String key;
    }

    public enum Exchange {
        DEFAULT,
        MAIL,
        MAIL_INTEGRATION;
    }

    public boolean convertAndSend(Routing routing, T eventMessage) {
        return convertAndSend(routing, eventMessage, setSupplierQueueNameHeader());
    }

    public boolean convertAndSend(Exchange exchange, Routing routing, T eventMessage) {
        return convertAndSend(exchange, routing, eventMessage, message -> message);
    }

    public boolean convertAndSend(Routing routing, T eventMessage, String headerName, String headerValue) {
        return convertAndSend(routing, eventMessage, message -> {
            message.getMessageProperties().setHeader(headerName, headerValue);
            return message;
        });
    }

    private boolean convertAndSend(Routing routing, T eventMessage, MessagePostProcessor messagePostProcessor) {
        return convertAndSend(Exchange.DEFAULT, routing, eventMessage, messagePostProcessor);
    }

    private boolean convertAndSend(Exchange exchange, Routing routing, T eventMessage, MessagePostProcessor messagePostProcessor) {
        try {
            String name = getExchangeName(exchange);
            String key = getRoutingKey(routing);
            rabbitTemplate.convertAndSend(name, key, eventMessage, messagePostProcessor);
            return true;
        } catch (AmqpException e) {
            return false;
        }
    }

    private MessagePostProcessor setSupplierQueueNameHeader() {
        return message -> {
            message.getMessageProperties().getHeaders().putIfAbsent(SUPPLIER_QUEUE_NAME_HEADER, messageBrokerService.getSettingQueueName());
            return message;
        };
    }


    public boolean isSettingQueueConsumer(Message message) {
        return messageBrokerService.getSettingQueueName().equals(getSupplierQueueNameHeader(message));
    }

    private String getSupplierQueueNameHeader(Message message) {
        Object supplier =  message.getMessageProperties().getHeaders().get(SUPPLIER_QUEUE_NAME_HEADER);
        return supplier != null ? message.getMessageProperties().getHeaders().get(SUPPLIER_QUEUE_NAME_HEADER).toString() : null;
    }

    private String getExchangeName(Exchange exchange) {
        String name = null;
        switch (exchange) {
            case DEFAULT:
                name = this.exchangeName;
                break;
            case MAIL:
                name = mailRoutingProps.getExchangeName();
                break;
            default:
                break;
        }
        return name;
    }

    private String getRoutingKey(Routing routing) {
        String key = null;
        switch (routing) {
            case SETTINGS:
            case ZFR_CALLBACKS:
            case ZBR_EVENTS:
            case TENANCIES:
                key = routing.key;
                break;
            case MAIL:
                key = mailRoutingProps.getKey();
                break;
            default:
                break;
        }
        return key;
    }

}
