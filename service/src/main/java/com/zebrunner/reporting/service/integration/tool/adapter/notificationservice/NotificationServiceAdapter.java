package com.zebrunner.reporting.service.integration.tool.adapter.notificationservice;

import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;

import java.util.Map;

public interface NotificationServiceAdapter extends IntegrationGroupAdapter {

    void sendNotification(TestRun tr, Map<String, String> notificationProperties);

    String getWebhook();
}
