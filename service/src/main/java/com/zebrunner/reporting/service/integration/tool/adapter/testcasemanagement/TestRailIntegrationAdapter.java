package com.zebrunner.reporting.service.integration.tool.adapter.testcasemanagement;

import com.zebrunner.reporting.domain.dto.IssueDTO;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;

public class TestRailIntegrationAdapter extends AbstractIntegrationAdapter implements TestCaseManagementAdapter {

    private final String url;

    public TestRailIntegrationAdapter(Integration integration) {
        super(integration);

        this.url = getAttributeValue(integration, TestRailParam.TESTRAIL_URL);
    }

    private enum TestRailParam implements AdapterParam {
        TESTRAIL_URL("TESTRAIL_URL");

        private final String name;

        TestRailParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        return url != null && !url.isBlank();
    }

    @Override
    public IssueDTO getIssue(String ticket) {
        return null;
    }

    @Override
    public boolean isIssueClosed(String ticket) {
        return false;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
