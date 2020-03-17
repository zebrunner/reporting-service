package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.mail.MailServiceAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.MailProxy;
import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Component
public class MailService extends AbstractIntegrationService<MailServiceAdapter> {

    public MailService(IntegrationService integrationService, MailProxy mailProxy) {
        super(integrationService, mailProxy, "EMAIL");
    }

    public CompletableFuture<Void> send(MimeMessagePreparator preparator) {
        MailServiceAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.send(preparator);
    }

    public void setFromAddress(MimeMessageHelper msg) throws MessagingException, UnsupportedEncodingException {
        MailServiceAdapter adapter = getAdapterByIntegrationId(null);
        String fromAddress = adapter.getFromAddress();
        String username = adapter.getUsername();
        if (!StringUtils.isBlank(fromAddress)) {
            msg.setFrom(fromAddress, username);
        } else {
            msg.setFrom(username);
        }
    }

}
