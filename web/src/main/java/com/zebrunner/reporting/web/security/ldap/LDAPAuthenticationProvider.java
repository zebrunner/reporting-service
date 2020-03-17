package com.zebrunner.reporting.web.security.ldap;

import com.zebrunner.reporting.service.exception.ExternalSystemException;
import com.zebrunner.reporting.service.integration.tool.impl.AccessManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;
import org.springframework.security.ldap.ppolicy.PasswordPolicyException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * Uses for load user from LDAP and recognize it.
 * Need override LdapAuthenticationProvider cause superclass has private getAuthenticator only
 * 
 * @author brutskov
 */
@Component
public class LDAPAuthenticationProvider extends AbstractLdapAuthenticationProvider {

    private static final String MSG_LDAP_DISABLED_OR_MISCONFIGURED = "Attemp to authenticate via LDAP while integration is disabled or misconfigured";

    private static final Logger LOGGER = LoggerFactory.getLogger(LDAPAuthenticationProvider.class);

    @Autowired
    private AccessManagementService accessManagementService;

    private final LDAPUserDetailsContextMapper ldapUserDetailsContextMapper;
    private final LdapAuthoritiesPopulator authoritiesPopulator;

    public LDAPAuthenticationProvider(LDAPUserDetailsContextMapper ldapUserDetailsContextMapper) {
        this.ldapUserDetailsContextMapper = ldapUserDetailsContextMapper;
        this.authoritiesPopulator = new NullLdapAuthoritiesPopulator();
    }

    @PostConstruct
    private void init() {
        super.setUserDetailsContextMapper(this.ldapUserDetailsContextMapper);
    }

    /**
     * Uses the same logic as superclass
     * 
     * @param authentication - authentication callback
     * @return context operation
     */
    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken authentication) {
        LdapAuthenticator ldapAuthenticator = getAuthenticator();
        if (ldapAuthenticator == null) {
            LOGGER.warn(MSG_LDAP_DISABLED_OR_MISCONFIGURED);
            throw new InternalAuthenticationServiceException(MSG_LDAP_DISABLED_OR_MISCONFIGURED);
        }
        try {
            return ldapAuthenticator.authenticate(authentication);
        } catch (PasswordPolicyException e) {
            LOGGER.debug(e.getMessage(), e);
            throw new LockedException(this.messages.getMessage(e.getStatus().getErrorCode(), e.getStatus().getDefaultMessage()));
        } catch (UsernameNotFoundException e) {
            LOGGER.debug(e.getMessage(), e);
            throw new ExternalSystemException(ExternalSystemException.ExternalSystemErrorDetail.LDAP_AUTHENTICATION_FAILED, this.messages.getMessage("LdapAuthenticationProvider.badCredentials", "Bad credentials"));
        } catch (NamingException e) {
            LOGGER.debug(e.getMessage(), e);
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
        // TODO by nsidorevich on 2019-09-19: add more generic catch?
    }

    private LdapAuthenticator getAuthenticator() {
        return accessManagementService.getBindAuthenticator();
    }

    @Override
    protected UserDetailsContextMapper getUserDetailsContextMapper() {
        return this.ldapUserDetailsContextMapper;
    }

    protected LdapAuthoritiesPopulator getAuthoritiesPopulator() {
        return this.authoritiesPopulator;
    }

    @Override
    protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData, String username, String password) {
        return getAuthoritiesPopulator().getGrantedAuthorities(userData, username);
    }
}
