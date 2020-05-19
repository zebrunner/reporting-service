package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.domain.push.events.EventMessage;
import com.zebrunner.reporting.service.integration.tool.impl.MessageBrokerService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventPushService {

    private static final String EXCHANGE_NAME = "events";
    private static final String SUPPLIER_QUEUE_NAME_HEADER = "SUPPLIER_QUEUE";

    private final RabbitTemplate rabbitTemplate;
    private final MessageBrokerService messageBrokerService;

    public EventPushService(RabbitTemplate rabbitTemplate, @Lazy MessageBrokerService messageBrokerService) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageBrokerService = messageBrokerService;
    }

    public enum Type {

        SETTINGS("settings"),
        ZBR_EVENTS("zbr_events"),
        TENANCIES("tenancies");

        private final String routingKey;

        Type(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getRoutingKey() {
            return routingKey;
        }
    }

    public <T extends EventMessage> boolean convertAndSend(Type type, T eventMessage) {
        return convertAndSend(type, eventMessage, setSupplierQueueNameHeader());
    }

    public <T extends EventMessage> boolean convertAndSend(Type type, T eventMessage, String headerName, String headerValue) {
        return convertAndSend(type, eventMessage, message -> {
            message.getMessageProperties().setHeader(headerName, headerValue);
            return message;
        });
    }

    private <T extends EventMessage> boolean convertAndSend(Type type, T eventMessage, MessagePostProcessor messagePostProcessor) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, type.getRoutingKey(), eventMessage, messagePostProcessor);
            return true;
        } catch (AmqpException e) {
            return false;
        }
    }

    public <T extends EventMessage> boolean sendFanout(String exchange, T message) {
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
                   .putIfAbsent(SUPPLIER_QUEUE_NAME_HEADER, messageBrokerService.getSettingQueueName());
            return message;
        };
    }


    public boolean isSettingQueueConsumer(Message message) {
        return messageBrokerService.getSettingQueueName().equals(getSupplierQueueNameHeader(message));
    }

    private String getSupplierQueueNameHeader(Message message) {
        Object supplier = message.getMessageProperties().getHeaders().get(SUPPLIER_QUEUE_NAME_HEADER);
        return supplier != null
                ? message.getMessageProperties().getHeaders().get(SUPPLIER_QUEUE_NAME_HEADER).toString()
                : null;
    }

}
