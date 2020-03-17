package com.zebrunner.reporting.service.integration.tool.adapter.mail;

import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.util.concurrent.CompletableFuture;

public interface MailServiceAdapter extends IntegrationGroupAdapter {

    CompletableFuture<Void> send(MimeMessagePreparator preparator);

    String getFromAddress();

    String getUsername();
}
