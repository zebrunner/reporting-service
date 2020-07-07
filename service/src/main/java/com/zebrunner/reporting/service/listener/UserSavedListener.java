package com.zebrunner.reporting.service.listener;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.push.events.UserSavedMessage;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.service.ExchangeConfig;
import com.zebrunner.reporting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSavedListener {

    private final UserService userService;
    private final MessageHelper messageHelper;

    @RabbitListener(queues = ExchangeConfig.USER_SAVED_REPORTING_SERVICE_QUEUE)
    public void handle(Message rabbitMessage) {
        UserSavedMessage message = messageHelper.parse(rabbitMessage, UserSavedMessage.class);

        User user = new User(message.getId());
        user.setUsername(message.getUsername());

        TenancyContext.setTenantName(message.getTenantName());
        if (!userService.existsById(user.getId())) {
            userService.create(user);
        }
    }

}
