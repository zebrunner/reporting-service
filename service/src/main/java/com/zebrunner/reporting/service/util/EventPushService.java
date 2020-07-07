package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.service.ExchangeConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class EventPushService<T> {

    private static final String SUPPLIER_QUEUE_NAME_HEADER = "SUPPLIER_QUEUE";

    private final RabbitTemplate rabbitTemplate;
    private final Map<String, Queue> queues;
    private final String exchangeName;

    public EventPushService(RabbitTemplate rabbitTemplate,
                            Map<String, Queue> queues,
                            @Value("${spring.rabbitmq.template.exchange}") String exchangeName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queues = queues;
        this.exchangeName = exchangeName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum Routing {

        SETTINGS("settings"),
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

    public boolean sendFanout(String exchange, T message) {
        try {
            rabbitTemplate.convertAndSend(exchange, "", message);
            return true;
        } catch (AmqpException e) {
            return false;
        }
    }

    public boolean send(String exchange, String routingKey, Object message, Map<String, ?> metadata) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message, rabbitmqMessage -> {
                metadata.forEach((key, value) -> rabbitmqMessage.getMessageProperties().setHeader(key, value));
                return rabbitmqMessage;
            });
            return true;
        } catch (AmqpException e) {
            return false;
        }
    }

    private MessagePostProcessor setSupplierQueueNameHeader() {
        return message -> {
            message.getMessageProperties()
                   .getHeaders()
                   .putIfAbsent(SUPPLIER_QUEUE_NAME_HEADER, getSettingQueueName());
            return message;
        };
    }


    public boolean isSettingQueueConsumer(Message message) {
        return getSettingQueueName().equals(getSupplierQueueNameHeader(message));
    }

    private String getSupplierQueueNameHeader(Message message) {
        Object supplier = message.getMessageProperties().getHeaders().get(SUPPLIER_QUEUE_NAME_HEADER);
        return supplier != null
                ? message.getMessageProperties().getHeaders().get(SUPPLIER_QUEUE_NAME_HEADER).toString()
                : null;
    }

    private String getExchangeName(Exchange exchange) {
        String name = null;
        switch (exchange) {
            case DEFAULT:
                name = this.exchangeName;
                break;
            case MAIL:
                name = ExchangeConfig.MAIL_DATA_EXCHANGE;
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
            case ZBR_EVENTS:
            case TENANCIES:
                key = routing.key;
                break;
            case MAIL:
                key = ExchangeConfig.MAIL_DATA_ROUTING_KEY;
                break;
            default:
                break;
        }
        return key;
    }

    public String getSettingQueueName() {
        return queues.get("settingsQueue").getName();
    }

}
