package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.domain.dto.IssueDTO;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.testcasemanagement.TestCaseManagementAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.TestCaseManagementProxy;
import org.springframework.stereotype.Component;

@Component
public class TestCaseManagementService extends AbstractIntegrationService<TestCaseManagementAdapter> {

    public TestCaseManagementService(IntegrationService integrationService, TestCaseManagementProxy testCaseManagementProxy) {
        super(integrationService, testCaseManagementProxy, "JIRA");
    }

    public IssueDTO getIssue(String ticket) {
        TestCaseManagementAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.getIssue(ticket);
    }

    // TODO: 10/2/19 url should not be obrained from adapter; use integration settings instead
    public String getUrl() {
        TestCaseManagementAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.getUrl();
    }

    public boolean isIssueClosed(String ticket) {
        TestCaseManagementAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.isIssueClosed(ticket);
    }

}
