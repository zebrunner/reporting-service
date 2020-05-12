package com.zebrunner.reporting.service.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MessageHelper {

    private final ObjectMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    public <T> T parse(Message message, Class<T> messageClass) {
        try {
            return mapper.readValue(message.getBody(), messageClass);
        } catch (IOException e) {
            throw new RuntimeException("AMPQ message is malformed");
        }
    }

    //Unstable
    public <T> T parse(Message message, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(message.getBody(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException("AMPQ message is malformed");
        }
    }

    public Object getHeader(Message message, String headerName) {
        return message.getMessageProperties().getHeaders().get(headerName);
    }

}
