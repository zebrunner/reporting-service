package com.zebrunner.reporting.domain.push.events;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public final class MailNotification extends EventMessage {

    private Set<String> recipients;
    private String subject;
    private String message;
    private String resourceKey;
    private Object content;
    private List<File> attachments;


    public MailNotification(String tenantName) {
        super(tenantName);
    }
}
