package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationTypeService;
import com.zebrunner.reporting.service.integration.core.IntegrationInitializer;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.impl.AutomationServerService;
import com.zebrunner.reporting.service.integration.tool.impl.NotificationService;
import com.zebrunner.reporting.service.integration.tool.impl.TestCaseManagementService;
import com.zebrunner.reporting.service.util.URLResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_RUN_NOT_FOUND;

@Service
public class ConfigurationService {

    private static final String ERR_MSG_TEST_RUN_NOT_FOUND = "Test run with id %s can not be found";
    private static final String ERR_MSG_TEST_INTEGRATION_NOT_FOUND = "Integration with name %s can not be found";

    private final URLResolver urlResolver;
    private final AutomationServerService automationServerService;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationInitializer integrationInitializer;
    private final TestCaseManagementService testCaseManagementService;
    private final TestRunService testRunService;
    private final NotificationService notificationService;

    @Value("${service.version}")
    private String serviceVersion;

    public ConfigurationService(
            URLResolver urlResolver,
            AutomationServerService automationServerService,
            IntegrationTypeService integrationTypeService,
            IntegrationInitializer integrationInitializer, TestCaseManagementService testCaseManagementService,
            TestRunService testRunService,
            NotificationService notificationService
    ) {
        this.urlResolver = urlResolver;
        this.automationServerService = automationServerService;
        this.integrationTypeService = integrationTypeService;
        this.integrationInitializer = integrationInitializer;
        this.testCaseManagementService = testCaseManagementService;
        this.testRunService = testRunService;
        this.notificationService = notificationService;
    }

    public Map<String, Object> getAppConfig() {
        return Map.of("service", serviceVersion, "service_url", urlResolver.buildWebserviceUrl());
    }

    public Map<String, Object> getJenkinsConfig() {
        boolean enabledAndConnected = automationServerService.isEnabledAndConnected(null);
        return Map.of("connected", enabledAndConnected);
    }

    public Map<String, Object> getJiraConfig() {
        boolean enabledAndConnected = testCaseManagementService.isEnabledAndConnected(null);
        return Map.of("connected", enabledAndConnected);
    }

    public Map<String, Object> getSlackConfigByTestRunId(Long testRunId) {
        TestRun testRun = testRunService.getTestRunByIdFull(testRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, String.format(ERR_MSG_TEST_RUN_NOT_FOUND, testRunId));
        }
        boolean available = isSlackAvailable() && StringUtils.isNotEmpty(testRun.getChannels());
        return Map.of("available", available);
    }

    public Map<String, Object> getSlackConfig() {
        boolean available = isSlackAvailable();
        return Map.of("available", available);
    }

    private boolean isSlackAvailable() {
        IntegrationType defaultType = integrationTypeService.retrieveByName(notificationService.getDefaultType());
        String integrationName = "SLACK";
        Integration slackIntegration = defaultType.getIntegrations()
                                                  .stream()
                                                  .filter(integration -> integrationName.equals(integration.getName()))
                                                  .findFirst()
                                                  .orElseThrow(() -> new ResourceNotFoundException(TEST_RUN_NOT_FOUND, String.format(ERR_MSG_TEST_INTEGRATION_NOT_FOUND, integrationName)));
        return isEnabledAndConnected(defaultType, slackIntegration);
    }

    private boolean isEnabledAndConnected(IntegrationType integrationType, Integration integration) {
        AbstractIntegrationService integrationService = integrationInitializer.getIntegrationServices().get(integrationType.getGroup().getName());
        boolean enabled = integration.isEnabled();
        boolean connected = false;
        if (enabled) {
            connected = integrationService.isEnabledAndConnected(integration.getId());
        }
        return enabled & connected;
    }
}
