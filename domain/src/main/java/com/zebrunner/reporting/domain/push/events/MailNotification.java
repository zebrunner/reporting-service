package com.zebrunner.reporting.domain.push.events;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public final class MailNotification {

    private Set<String> recipients;
    private String subject;
    private String message;
    private String templateName;
    private Object content;
    private List<File> attachments;

}
