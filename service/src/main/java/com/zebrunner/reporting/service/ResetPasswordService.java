package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.CREDENTIALS_RESET_IS_NOT_POSSIBLE;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private static final String USER_FOR_PASSWORD_RESET_IS_NOT_FOUND = "User for password reset is not found";

    private final EmailService emailService;
    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public void sendResetPasswordEmail(String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new IllegalOperationException(CREDENTIALS_RESET_IS_NOT_POSSIBLE, USER_FOR_PASSWORD_RESET_IS_NOT_FOUND);
        }
        if (User.Source.INTERNAL.equals(user.getSource())) {
            String token = RandomStringUtils.randomAlphanumeric(50);
            userService.updateResetToken(token, user.getId());
            emailService.sendForgotPasswordEmail(token, email);
        } else {
            emailService.sendForgotPasswordLdapEmail(null, email);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String token, String password) {
        User user = userService.getUserByResetToken(token);
        userService.updateUserPassword(user, password);
        userService.updateResetToken(null, user.getId());
    }

}
