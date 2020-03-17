package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.messagebroker.MessageBrokerAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.MessageBrokerProxy;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageBrokerService extends AbstractIntegrationService<MessageBrokerAdapter> {

    private final Map<String, Queue> queues;

    public MessageBrokerService(IntegrationService integrationService, MessageBrokerProxy messageBrokerProxy, Map<String, Queue> queues) {
        super(integrationService, messageBrokerProxy, "RABBITMQ");
        this.queues = queues;
    }

    public String getSettingQueueName() {
        return queues.get("settingsQueue").getName();
    }
}
