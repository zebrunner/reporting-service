package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.domain.entity.integration.IntegrationType;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.IntegrationTypeService;
import com.zebrunner.reporting.service.integration.tool.impl.AutomationServerService;
import com.zebrunner.reporting.service.integration.tool.impl.NotificationService;
import com.zebrunner.reporting.service.integration.tool.impl.TestCaseManagementService;
import com.zebrunner.reporting.service.util.URLResolver;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_NOT_FOUND;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_RUN_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private static final String ERR_MSG_TEST_RUN_NOT_FOUND = "Test run with id %s can not be found";
    private static final String ERR_MSG_TEST_INTEGRATION_NOT_FOUND = "Integration with name %s can not be found";

    private final URLResolver urlResolver;
    private final AutomationServerService automationServerService;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationService integrationService;
    private final TestCaseManagementService testCaseManagementService;
    private final TestRunService testRunService;
    private final NotificationService notificationService;

    @Value("${service.version}")
    private String serviceVersion;

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
        List<Integration> integrations = integrationService.retrieveIntegrationsByGroupId(defaultType.getGroup().getId());
        String integrationName = "SLACK";
        Integration slackIntegration = integrations.stream()
                                                   .filter(integration -> integrationName.equals(integration.getName()))
                                                   .findFirst()
                                                   .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_NOT_FOUND, String.format(ERR_MSG_TEST_INTEGRATION_NOT_FOUND, integrationName)));

        return notificationService.isEnabledAndConnected(slackIntegration.getId());
    }

}
