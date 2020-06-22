package com.zebrunner.reporting.domain.push.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "noArgBuilder")
public final class MailMessage {

    private String templateName;
    private Set<String> toEmails;
    private Set<String> ccEmails;
    private Set<String> bccEmails;
    private String subject;
    private String body;
    private Map<String, Object> mailData;
    private List<Attachment> attachments;

    public static MailMessageBuilder builder(String templateName, String subject, Set<String> toEmails, Map<String, Object> mailData) {
        return noArgBuilder().templateName(templateName)
                             .subject(subject)
                             .toEmails(toEmails)
                             .mailData(mailData);
    }

}
