package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.service.email.AbstractEmail;
import com.zebrunner.reporting.service.email.ResetPasswordEmail;
import com.zebrunner.reporting.service.email.ResetPasswordLdapEmail;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.util.URLResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.CREDENTIALS_RESET_IS_NOT_POSSIBLE;

@Service
public class ResetPasswordService {

    private static final String USER_FOR_PASSWORD_RESET_IS_NOT_FOUND = "User for password reset is not found";

    private final String slackImageUrl;
    private final URLResolver urlResolver;
    private final EmailService emailService;
    private final UserService userService;

    public ResetPasswordService(
            @Value("${slack.image-url}") String slackImageUrl,
            URLResolver urlResolver,
            EmailService emailService,
            UserService userService
    ) {
        this.slackImageUrl = slackImageUrl;
        this.urlResolver = urlResolver;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendResetPasswordEmail(String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new IllegalOperationException(CREDENTIALS_RESET_IS_NOT_POSSIBLE, USER_FOR_PASSWORD_RESET_IS_NOT_FOUND);
        }
        AbstractEmail emailMessage;
        if (User.Source.INTERNAL.equals(user.getSource())) {
            String token = RandomStringUtils.randomAlphanumeric(50);
            userService.updateResetToken(token, user.getId());
            emailMessage = new ResetPasswordEmail(token, slackImageUrl, urlResolver.buildWebURL());
        } else {
            emailMessage = new ResetPasswordLdapEmail(slackImageUrl, urlResolver.buildWebURL());
        }
        emailService.sendEmail(emailMessage, email);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String token, String password) {
        User user = userService.getUserByResetToken(token);
        userService.updateUserPassword(user, password);
        userService.updateResetToken(null, user.getId());
    }

}
