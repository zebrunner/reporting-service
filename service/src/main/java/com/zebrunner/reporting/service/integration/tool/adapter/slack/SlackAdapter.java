package com.zebrunner.reporting.service.integration.tool.adapter.slack;

import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;

public interface SlackAdapter extends IntegrationGroupAdapter {

    void sendNotification(TestRun tr, String customizedMessage);

    String getWebhook();
}
