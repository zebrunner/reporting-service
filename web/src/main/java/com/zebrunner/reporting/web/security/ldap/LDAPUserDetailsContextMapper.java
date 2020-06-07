package com.zebrunner.reporting.web.security.ldap;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.dto.auth.AuthenticatedUser;
import com.zebrunner.reporting.service.UserService;
import com.zebrunner.reporting.service.exception.ForbiddenOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class LDAPUserDetailsContextMapper implements UserDetailsContextMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LDAPUserDetailsContextMapper.class);

    private static final String MSG_ILLEGAL_USER_LOGIN = "User %s is not an invited Zebrunner user";
    private static final String WRN_MSG_NON_EXISTING_USER = "Existing LDAP user %s can't be logged in because he does not exists in Zebrunner";

    private final UserService userService;

    public LDAPUserDetailsContextMapper(UserService userService) {
        this.userService = userService;
    }

    /**
     * Mapping LDAP user to Zebrunner user. If LDAP user is not a Zebrunner user (meaning that he was found in LDAP
     * but was not invited to Zebrunner)
     */
    @Override
    public UserDetails mapUserFromContext(DirContextOperations operations, String username, Collection<? extends GrantedAuthority> authorities) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            LOGGER.warn(String.format(WRN_MSG_NON_EXISTING_USER, username));
            throw new ForbiddenOperationException(String.format(MSG_ILLEGAL_USER_LOGIN, username));
        } else {
            return new AuthenticatedUser(user.getId(), username, user.getPassword(), user.getPermissions());
        }
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter adapter) {
        // Do nothing
    }
}
