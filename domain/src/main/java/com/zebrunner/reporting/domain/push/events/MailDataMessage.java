package com.zebrunner.reporting.domain.push.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object aggregating mail message data, that will be used to construct mail message to be sent.
 * Message can contain plain text only, or template name and template model, or all three (in such cases multipart
 * message will be sent containing both plain text and html).
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public final class MailDataMessage {

    private final Set<String> toEmails;
    private final Set<String> ccEmails;
    private final Set<String> bccEmails;
    private final String subject;
    private final String plainText;
    private final String templateName;
    private final Map<String, Object> templateModel;
    private final List<Attachment> attachments;

}
