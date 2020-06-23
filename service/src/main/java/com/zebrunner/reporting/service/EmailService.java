package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.push.events.Attachment;
import com.zebrunner.reporting.domain.push.events.MailDataMessage;
import com.zebrunner.reporting.service.exception.ProcessingException;
import com.zebrunner.reporting.service.util.EventPushService;
import com.zebrunner.reporting.service.util.FreemarkerUtil;
import com.zebrunner.reporting.service.util.URLResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.zebrunner.reporting.service.exception.ProcessingException.ProcessingErrorDetail.UNPROCESSABLE_INPUT_STREAM;

@Slf4j
@Component
public class EmailService {

    private static final String ERR_MSG_UNABLE_TO_CREATE_INPUT_STREAM = "Unable to create input stream from file";

    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String DASHBOARD_TEMPLATE_NAME = "dashboard.ftl";

    private static final String TEST_RUN_RESULT_TEMPLATE_NAME = "test_run_results.ftl";

    private static final String USER_INVITATION_TEMPLATE_NAME = "invitation.ftl";
    private static final String USER_INVITATION_MAIL_SUBJECT = "Join the workspace";

    private static final String USER_INVITATION_LDAP_TEMPLATE_NAME = "invitation_ldap.ftl";
    private static final String USER_INVITATION_LDAP_MAIL_SUBJECT = "Join the workspace";

    private static final String FORGOT_PASSWORD_TEMPLATE_NAME = "forgot_password.ftl";
    private static final String FORGOT_PASSWORD_MAIL_SUBJECT = "Password reset";

    private static final String FORGOT_PASSWORD_LDAP_TEMPLATE_NAME = "forgot_password_ldap.ftl";
    private static final String FORGOT_PASSWORD_LDAP_MAIL_SUBJECT = "Password reset";

    private final EventPushService<MailDataMessage> eventPushService;
    private final FreemarkerUtil freemarkerUtil;
    private final URLResolver urlResolver;
    private final String logoUrl;

    public EmailService(EventPushService<MailDataMessage> eventPushService,
                        FreemarkerUtil freemarkerUtil,
                        URLResolver urlResolver,
                        @Value("${slack.image-url}") String logoUrl) {
        this.eventPushService = eventPushService;
        this.freemarkerUtil = freemarkerUtil;
        this.urlResolver = urlResolver;
        this.logoUrl = logoUrl;
    }

    public String sendDashboardEmail(String subject, String body, List<File> files, String... emails) {
        List<Attachment> attachments = convertFilesToAttachments(files);

        Map<String, Object> templateModel = new HashMap<>(templateModel());
        templateModel.put("text", body);
        templateModel.put("subject", subject);
        templateModel.put("attachments", attachments);

        MailDataMessage mailDataMessage = MailDataMessage.builder()
                                                         .templateName(DASHBOARD_TEMPLATE_NAME)
                                                         .subject(subject)
                                                         .toEmails(Set.of(emails))
                                                         .templateModel(templateModel)
                                                         .attachments(attachments)
                                                         .build();
        return sendEmail(mailDataMessage);
    }

    public String sendTestRunResultsEmail(TestRun testRun, List<Test> tests, String jiraUrl, boolean showOnlyFailures, boolean showStacktrace, boolean showJenkinsUrl, int successRate, String... emails) {
        String status = buildStatusText(testRun);
        String subject = String.format("%s: %s", status, testRun.getName());

        Map<String, Object> templateModel = buildTestRunResultsTemplateModel(testRun, tests, jiraUrl, showOnlyFailures, showStacktrace, showJenkinsUrl, successRate);

        MailDataMessage mailDataMessage = MailDataMessage.builder()
                                                         .templateName(TEST_RUN_RESULT_TEMPLATE_NAME)
                                                         .subject(subject)
                                                         .toEmails(Set.of(emails))
                                                         .templateModel(templateModel)
                                                         .build();
        return sendEmail(mailDataMessage);
    }

    public Map<String, Object> buildTestRunResultsTemplateModel(TestRun testRun, List<Test> tests, String jiraUrl, boolean showOnlyFailures, boolean showStacktrace, boolean showJenkinsUrl, int successRate) {
        String elapsed = testRun.getElapsed() != null ? LocalTime.ofSecondOfDay(testRun.getElapsed()).toString() : null;
        Map<String, Object> mailData = new HashMap<>(templateModel());
        mailData.put("testRun", testRun);
        mailData.put("tests", tests);
        mailData.put("jiraURL", jiraUrl);
        mailData.put("showOnlyFailures", showOnlyFailures);
        mailData.put("showStacktrace", showStacktrace);
        mailData.put("showJenkinsUrl", showJenkinsUrl);
        mailData.put("successRate", successRate);
        mailData.put("elapsed", elapsed);
        return mailData;
    }

    public static String buildStatusText(TestRun testRun) {
        return Status.PASSED.equals(testRun.getStatus()) && testRun.isKnownIssue() && !testRun.isBlocker() ? "PASSED (known issues)"
                : testRun.isBlocker() ? "FAILED (BLOCKERS)" : testRun.getStatus().name();
    }

    public String sendUserInvitationEmail(String invitationUrl, String... emails) {
        Map<String, Object> templateModel = new HashMap<>(templateModel());
        templateModel.put("invitationUrl", invitationUrl);
        MailDataMessage mailDataMessage = MailDataMessage.builder()
                                                         .templateName(USER_INVITATION_TEMPLATE_NAME)
                                                         .subject(USER_INVITATION_MAIL_SUBJECT)
                                                         .toEmails(Set.of(emails))
                                                         .templateModel(templateModel)
                                                         .build();
        return sendEmail(mailDataMessage);
    }

    public String sendUserInvitationLdapEmail(String invitationUrl, String... emails) {
        Map<String, Object> templateModel = new HashMap<>(templateModel());
        templateModel.put("invitationUrl", invitationUrl);
        MailDataMessage mailDataMessage = MailDataMessage.builder()
                                                         .templateName(USER_INVITATION_LDAP_TEMPLATE_NAME)
                                                         .subject(USER_INVITATION_LDAP_MAIL_SUBJECT)
                                                         .toEmails(Set.of(emails))
                                                         .templateModel(templateModel)
                                                         .build();
        return sendEmail(mailDataMessage);
    }

    public String sendForgotPasswordEmail(String token, String... emails) {
        Map<String, Object> templateModel = new HashMap<>(templateModel());
        templateModel.put("token", token);
        MailDataMessage mailDataMessage = MailDataMessage.builder()
                                                         .templateName(FORGOT_PASSWORD_TEMPLATE_NAME)
                                                         .subject(FORGOT_PASSWORD_MAIL_SUBJECT)
                                                         .toEmails(Set.of(emails))
                                                         .templateModel(templateModel)
                                                         .build();
        return sendEmail(mailDataMessage);
    }

    public String sendForgotPasswordLdapEmail(String token, String... emails) {
        Map<String, Object> templateModel = new HashMap<>(templateModel());
        templateModel.put("token", token);
        MailDataMessage mailDataMessage = MailDataMessage.builder()
                                                         .templateName(FORGOT_PASSWORD_LDAP_TEMPLATE_NAME)
                                                         .subject(FORGOT_PASSWORD_LDAP_MAIL_SUBJECT)
                                                         .toEmails(Set.of(emails))
                                                         .templateModel(templateModel)
                                                         .build();
        return sendEmail(mailDataMessage);
    }

    private Map<String, Object> templateModel() {
        return Map.of(
                "logoUrl", logoUrl,
                "workspaceURL", urlResolver.buildWebURL()
        );
    }

    public String sendEmail(MailDataMessage mailDataMessage) {
        Set<String> toEmails = processRecipients(mailDataMessage.getToEmails());
        if (!toEmails.isEmpty()) {
            eventPushService.convertAndSend(EventPushService.Exchange.MAIL, EventPushService.Routing.MAIL, mailDataMessage);
        }
        return freemarkerUtil.processEmailFreemarkerTemplateFromS3(mailDataMessage.getTemplateName(), mailDataMessage.getTemplateModel());
    }

    private List<Attachment> convertFilesToAttachments(List<File> files) {
        return files.stream().map(file -> {
            try {
                return new Attachment(file.getName(), FileUtils.readFileToByteArray(file));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new ProcessingException(UNPROCESSABLE_INPUT_STREAM, ERR_MSG_UNABLE_TO_CREATE_INPUT_STREAM);
            }
        }).collect(Collectors.toList());
    }

    private Set<String> processRecipients(Set<String> emails) {
        return emails.stream().filter(email -> {
            boolean isValid = isValid(email);
            if (!isValid) {
                log.info("Not valid recipient specified: " + email);
            }
            return isValid;
        }).collect(Collectors.toSet());
    }

    private boolean isValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static String getTestRunResultTemplateName() {
        return TEST_RUN_RESULT_TEMPLATE_NAME;
    }
}
