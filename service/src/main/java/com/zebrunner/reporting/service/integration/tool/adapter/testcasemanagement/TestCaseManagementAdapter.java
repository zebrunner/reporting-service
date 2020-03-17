package com.zebrunner.reporting.service.integration.tool.adapter.testcasemanagement;

import com.zebrunner.reporting.domain.dto.IssueDTO;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;

public interface TestCaseManagementAdapter extends IntegrationGroupAdapter {

    IssueDTO getIssue(String ticket);

    String getUrl();

    boolean isIssueClosed(String ticket);

}
