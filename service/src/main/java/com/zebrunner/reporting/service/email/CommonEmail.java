package com.zebrunner.reporting.service.email;

import com.zebrunner.reporting.domain.db.Attachment;

import java.util.List;

public class CommonEmail implements IEmailMessage {

    private String subject;
    private String text;
    private List<Attachment> attachments;

    public CommonEmail(String subject, String text, List<Attachment> attachments) {
        this.subject = subject;
        this.text = text;
        this.attachments = attachments;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public EmailType getType() {
        return EmailType.DASHBOARD;
    }

    @Override
    public List<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public String getText() {
        return text;
    }
}
