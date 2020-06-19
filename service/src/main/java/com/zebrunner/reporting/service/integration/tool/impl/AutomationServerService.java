package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.persistence.repository.IntegrationSettingRepository;
import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.dto.BuildParameterType;
import com.zebrunner.reporting.domain.dto.JobResult;
import com.zebrunner.reporting.domain.entity.integration.IntegrationSetting;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.automationserver.AutomationServerAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.AutomationServerProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class AutomationServerService extends AbstractIntegrationService<AutomationServerAdapter> {

    private final IntegrationSettingRepository integrationSettingRepository;

    public AutomationServerService(IntegrationService integrationService, AutomationServerProxy automationServerProxy, IntegrationSettingRepository integrationSettingRepository) {
        super(integrationService, automationServerProxy, "JENKINS");
        this.integrationSettingRepository = integrationSettingRepository;
    }

    public void rerunJob(Job job, Integer buildNumber, boolean rerunFailures) {
        String jobURL = job.getJobURL();
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        Map<String, String> params = adapter.getBuildParametersMap(jobURL, buildNumber);

        params.put("rerun_failures", Boolean.toString(rerunFailures));
        params.replace("debug", "false");

        adapter.buildJob(jobURL, params);
    }

    public void debugJob(Job job, Integer buildNumber) {
        String jobURL = job.getJobURL();
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        Map<String, String> params = adapter.getBuildParametersMap(job.getJobURL(), buildNumber);

        params.replace("debug", "true");
        params.replace("rerun_failures", "true");
        params.replace("thread_count", "1");

        adapter.buildJob(jobURL, params);
    }

    public void buildJob(Job job, Map<String, String> jobParameters) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        adapter.buildJob(job.getJobURL(), jobParameters);
    }

    public JobResult buildScannerJob(String repositoryName, Map<String, String> jobParameters, boolean rescan, Long automationServerId) {
        log.info("AUTOMATION_SERVER_ID: " + automationServerId);
        log.info("RESCAN: " + rescan);
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        log.info("ADAPTER: " + adapter);
        String jobUrl = adapter.buildScannerJobUrl(repositoryName, rescan);
        return adapter.buildJob(jobUrl, jobParameters);
    }

    public void abortScannerJob(String repositoryName, Integer buildNumber, boolean rescan, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        String jobUrl = adapter.buildScannerJobUrl(repositoryName, rescan);
        adapter.abortJob(jobUrl, buildNumber);
    }

    public void abortJob(Job job, Integer buildNumber) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        adapter.abortJob(job.getJobURL(), buildNumber);
    }

    public boolean isScannerJobInProgress(String repositoryName, Integer buildNumber, boolean rescan, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        String jobUrl = adapter.buildScannerJobUrl(repositoryName, rescan);
        return adapter.isBuildInProgress(jobUrl, buildNumber);
    }

    public List<BuildParameterType> getBuildParameters(Job job, Integer buildNumber) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        return adapter.getBuildParameters(job, buildNumber);
    }

    public Map<Integer, String> getBuildConsoleOutput(Job job, Integer buildNumber, Integer stringsCount, Integer fullCount) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        return adapter.getBuildConsoleOutput(job, buildNumber, stringsCount, fullCount);
    }

    public Integer getBuildNumber(String queueItemUrl, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getBuildNumber(queueItemUrl);
    }

    public Job getJobByUrl(String jobUrl, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getJobDetailsFromJenkins(jobUrl);
    }

    public String buildLauncherJobUrl(Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.buildLauncherJobUrl();
    }

    public String getUrl(Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getUrl();
    }

    public String getFolder(Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getFolder();
    }

    public boolean jobUrlVisibilityEnabled(Long integrationId) {
        Optional<IntegrationSetting> maybeSetting = getSetting(integrationId, "JENKINS_JOB_URL_VISIBILITY");
        return maybeSetting.isPresent() && Boolean.parseBoolean(maybeSetting.get().getValue());
    }

    public boolean showJobUrl(Long integrationId) {
        return integrationId == null || jobUrlVisibilityEnabled(integrationId);
    }

    private Optional<IntegrationSetting> getSetting(Long integrationId, String name) {
        Optional<IntegrationSetting> maybeSetting;
        if (integrationId == null) {
            maybeSetting = integrationSettingRepository.findByIntegrationTypeNameAndParamName(getDefaultType(), name);
        } else {
            maybeSetting = integrationSettingRepository.findByIntegrationIdAndParamName(integrationId, name);
        }
        return maybeSetting;
    }

}
