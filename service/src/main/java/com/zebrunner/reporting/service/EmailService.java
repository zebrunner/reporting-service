package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.Attachment;
import com.zebrunner.reporting.domain.dto.EmailType;
import com.zebrunner.reporting.domain.push.events.MailMessage;
import com.zebrunner.reporting.service.email.CommonEmail;
import com.zebrunner.reporting.service.email.IEmailMessage;
import com.zebrunner.reporting.service.util.EmailUtils;
import com.zebrunner.reporting.service.util.EventPushService;
import com.zebrunner.reporting.service.util.FreemarkerUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final EventPushService<MailMessage> eventPushService;
    private final FreemarkerUtil freemarkerUtil;

    public String sendEmail(final IEmailMessage message, final String... emails) {
        final String[] recipients = processRecipients(emails);
        String templateName = null;
        if (!ArrayUtils.isEmpty(recipients)) {
            MailMessage mailMessage = new MailMessage();
            mailMessage.setSubject(message.getSubject());
            mailMessage.setBody(message.getText());
            mailMessage.setContent(message);

            boolean hasAttachments = message.getAttachments() != null;
            if (hasAttachments) {
                List<File> attachments = message.getAttachments().stream()
                                                .map(Attachment::getFile)
                                                .collect(Collectors.toList());
                mailMessage.setAttachments(attachments);
            }
            mailMessage.setRecipients(Set.of(recipients));

            templateName = message.getType().getTemplateName();
            mailMessage.setTemplateName(templateName);
            eventPushService.convertAndSend(EventPushService.Exchange.MAIL, EventPushService.Routing.MAIL, mailMessage);
        }
        return freemarkerUtil.processEmailFreemarkerTemplateFromS3(templateName, message);
    }

    public String sendEmail(EmailType email, File file, String filename) {
        Attachment attachment = new Attachment(email.getSubject(), file, filename);
        List<Attachment> attachments = List.of(attachment);
        String[] emails = EmailUtils.obtainRecipients(email.getRecipients());
        IEmailMessage message = new CommonEmail(email.getSubject(), email.getText(), attachments);

        return sendEmail(message, emails);
    }

    private String[] processRecipients(String... emails) {
        return Arrays.stream(emails).filter(email -> {
            boolean isValid = isValid(email);
            if (!isValid) {
                LOGGER.info("Not valid recipient specified: " + email);
            }
            return isValid;
        }).toArray(String[]::new);
    }

    private boolean isValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
