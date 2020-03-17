package com.zebrunner.reporting.service.email;

import com.zebrunner.reporting.domain.db.Attachment;

import java.util.List;

public interface IEmailMessage {
    String getSubject();

    String getText();

    EmailType getType();

    List<Attachment> getAttachments();
}
