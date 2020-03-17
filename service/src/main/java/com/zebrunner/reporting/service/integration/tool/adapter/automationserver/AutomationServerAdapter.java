package com.zebrunner.reporting.service.integration.tool.adapter.automationserver;

import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.dto.BuildParameterType;
import com.zebrunner.reporting.domain.dto.JobResult;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;

import java.util.List;
import java.util.Map;

public interface AutomationServerAdapter extends IntegrationGroupAdapter {

    JobResult buildJob(String jobURL, Map<String, String> jobParameters);

    void abortJob(String jobURL, Integer buildNumber);

    String buildLauncherJobUrl();

    List<BuildParameterType> getBuildParameters(Job ciJob, Integer buildNumber);

    Map<String, String> getBuildParametersMap(String ciJobURL, Integer buildNumber);

    Map<Integer, String> getBuildConsoleOutput(Job ciJob, Integer buildNumber, Integer stringsCount, Integer fullCount);

    Integer getBuildNumber(String queueItemUrl);

    Job getJobDetailsFromJenkins(String jobUrl);

    String getUrl();

    String getFolder();

    String buildScannerJobUrl(String repositoryName, boolean rescan);

}
