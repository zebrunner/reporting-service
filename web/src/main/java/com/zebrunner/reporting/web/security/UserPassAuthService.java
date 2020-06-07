package com.zebrunner.reporting.web.security;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.dto.auth.AuthenticatedUser;
import com.zebrunner.reporting.service.UserService;
import com.zebrunner.reporting.service.exception.ForbiddenOperationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserPassAuthService implements UserDetailsService {

    private final UserService userService;

    public UserPassAuthService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = userService.getUserByUsernameOrEmail(username);
            if (user == null) {
                throw new Exception("Invalid username or email " + username);
            }
            if (user.getStatus().equals(User.Status.INACTIVE)) {
                throw new ForbiddenOperationException("User was blocked by admin.");
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found", e);
        }
        return new AuthenticatedUser(user.getId(), username, user.getPassword(), user.getPermissions());
    }
}
