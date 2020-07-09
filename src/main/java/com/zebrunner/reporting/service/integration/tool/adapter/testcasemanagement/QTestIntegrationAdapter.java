package com.zebrunner.reporting.service.integration.tool.adapter.testcasemanagement;

import com.zebrunner.reporting.domain.dto.IssueDTO;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;

public class QTestIntegrationAdapter extends AbstractIntegrationAdapter implements TestCaseManagementAdapter {

    private final String url;

    public QTestIntegrationAdapter(Integration integration) {
        super(integration);

        this.url = getAttributeValue(integration, QTestParam.QTEST_URL);
    }

    private enum QTestParam implements AdapterParam {
        QTEST_URL("QTEST_URL");

        private final String name;

        QTestParam(String name) {
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
