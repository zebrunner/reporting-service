package com.zebrunner.reporting.service.listener;

import com.zebrunner.reporting.service.EmailService;
import com.zebrunner.reporting.service.ExchangeConfig;
import com.zebrunner.reporting.service.email.EmailType;
import com.zebrunner.reporting.service.email.IEmailMessage;
import com.zebrunner.reporting.service.email.ResetPasswordEmail;
import com.zebrunner.reporting.service.email.ResetPasswordLdapEmail;
import com.zebrunner.reporting.service.email.SendEmailMessage;
import com.zebrunner.reporting.service.util.URLResolver;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SendEmailListener {

    @Setter(onMethod = @__(@Value("${slack.image-url}")))
    private String slackImageUrl;
    private final URLResolver urlResolver;
    private final EmailService emailService;
    private final MessageHelper messageHelper;

    private final Map<EmailType, Function<SendEmailMessage, IEmailMessage>> supportedEmailTypesToConverter = Map.of(
            EmailType.FORGOT_PASSWORD, this::toResetPasswordEmail,
            EmailType.FORGOT_PASSWORD_LDAP, this::toResetPasswordLdapEmail
    );

    @RabbitListener(queues = ExchangeConfig.SEND_EMAIL_QUEUE)
    private void sendEmail(Message message) {
        SendEmailMessage sendEmailMessage = messageHelper.parse(message, SendEmailMessage.class);

        Function<SendEmailMessage, IEmailMessage> converter =
                supportedEmailTypesToConverter.get(sendEmailMessage.getEmailType());

        if (converter != null) {
            IEmailMessage emailMessage = converter.apply(sendEmailMessage);
            emailService.sendEmail(emailMessage, sendEmailMessage.getTo());
        }
    }

    private ResetPasswordEmail toResetPasswordEmail(SendEmailMessage sendEmailMessage) {
        return new ResetPasswordEmail(
                sendEmailMessage.getToken(),
                slackImageUrl,
                urlResolver.buildWebURL()
        );
    }

    private ResetPasswordLdapEmail toResetPasswordLdapEmail(SendEmailMessage sendEmailMessage) {
        return new ResetPasswordLdapEmail(
                slackImageUrl,
                urlResolver.buildWebURL()
        );
    }

}
