package com.zebrunner.reporting.service.integration.tool.adapter.accessmanagement;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

public class LdapIntegrationAdapter extends AbstractIntegrationAdapter implements AccessManagementAdapter {

    private final String url;
    private final String managerUser;
    private final String managerPassword;
    private final String dn;
    private final String searchFilter;

    private final LdapContextSource ldapContextSource;
    private final BindAuthenticator bindAuthenticator;
    private final FilterBasedLdapUserSearch filterBasedLdapUserSearch;

    public LdapIntegrationAdapter(Integration integration) {
        super(integration);

        this.url = getAttributeValue(integration, LdapParam.LDAP_URL);
        this.managerUser = getAttributeValue(integration, LdapParam.LDAP_MANAGER_USER);
        this.managerPassword = getAttributeValue(integration, LdapParam.LDAP_MANAGER_PASSWORD);
        this.dn = getAttributeValue(integration, LdapParam.LDAP_DN);
        this.searchFilter = getAttributeValue(integration, LdapParam.LDAP_SEARCH_FILTER);

        this.ldapContextSource = new LdapContextSource();
        this.ldapContextSource.setUrl(url);
        this.ldapContextSource.setUserDn(managerUser);
        this.ldapContextSource.setPassword(managerPassword);
        this.ldapContextSource.afterPropertiesSet();
        this.filterBasedLdapUserSearch = new FilterBasedLdapUserSearch(dn, searchFilter, ldapContextSource);
        this.filterBasedLdapUserSearch.setSearchSubtree(true);
        this.bindAuthenticator = new BindAuthenticator(this.ldapContextSource);
        this.bindAuthenticator.setUserSearch(this.filterBasedLdapUserSearch);
    }

    private enum LdapParam implements AdapterParam {
        LDAP_URL("LDAP_URL"),
        LDAP_MANAGER_USER("LDAP_MANAGER_USER"),
        LDAP_MANAGER_PASSWORD("LDAP_MANAGER_PASSWORD"),
        LDAP_DN("LDAP_DN"),
        LDAP_SEARCH_FILTER("LDAP_SEARCH_FILTER");

        private final String name;

        LdapParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        try {
            ldapContextSource.getContext(ldapContextSource.getUserDn(), ldapContextSource.getPassword());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isUserExists(String username) {
        return filterBasedLdapUserSearch.searchForUser(username) != null;
    }

    public String getUrl() {
        return url;
    }

    public String getManagerUser() {
        return managerUser;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public String getDn() {
        return dn;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public LdapContextSource getLdapContextSource() {
        return ldapContextSource;
    }

    @Override
    public BindAuthenticator getBindAuthenticator() {
        return bindAuthenticator;
    }

    public FilterBasedLdapUserSearch getFilterBasedLdapUserSearch() {
        return filterBasedLdapUserSearch;
    }
}
