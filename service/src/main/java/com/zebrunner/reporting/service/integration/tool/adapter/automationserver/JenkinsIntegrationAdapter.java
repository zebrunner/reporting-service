package com.zebrunner.reporting.service.integration.tool.adapter.automationserver;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.ExtractHeader;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.dto.BuildParameterType;
import com.zebrunner.reporting.domain.dto.JobResult;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.JobsService;
import com.zebrunner.reporting.service.exception.ExternalSystemException;
import com.zebrunner.reporting.service.exception.IntegrationException;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.util.JenkinsClient;
import com.zebrunner.reporting.service.util.JenkinsConfig;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JenkinsIntegrationAdapter extends AbstractIntegrationAdapter implements AutomationServerAdapter {

    private static final String LAUNCHER_JOB_URL_PATTERN = "%s/job/%s/job/launcher";
    private static final String LAUNCHER_JOB_ROOT_URL_PATTERN = "%s/job/launcher";
    private static final String ERR_MSG_UNABLE_TO_BUILD_JOB_WITH_PARAMETERS = "Unable to build job '%s' with parameters %s";
    private static final String ERR_MSG_UNABLE_TO_ABORT_JOB = "Unable to abort job '%s'";
    private static final String ERR_MSG_UNABLE_TO_STOP_JOB = "Unable to stop job '%s'";
    private static final String ERR_MSG_UNABLE_TO_TERMINATE_JOB = "Unable to terminate job '%s'";
    private static final String ERR_MSG_UNABLE_TO_GET_CONSOLE_OUTPUT_FROM_BUILD = "Unable to get console output from build %s";
    private static final String ERR_MSG_UNABLE_TO_GET_QUEUE_ITEM_BY_REFERENCE = "Unable to get QueueItem by reference %s";
    private static final String ERR_MSG_UNABLE_TO_GET_BUILD_OBJECT_BY_QUEUE_ITEM = "Unable to get build by queueItem %s";
    private static final String ERR_MSG_UNABLE_TO_GET_BUILD_FROM_JOB = "Unable to get build %s from job '%s'";
    private static final String ERR_MSG_UNABLE_TO_GET_JOB_BY_URL = "Unable to get job by url '%s'";
    private static final String ERR_MSG_UNABLE_TO_GET_JOB_FROM_FOLDER = "Unable to get job '%s' from folder '%s";
    private static final String ERR_MSG_UNABLE_TO_GET_JOB_BUILD_DETAILS = "Unable to get job '%s' build %s details";

    private static final String SCANNER_JOB_URL_PATTERN = "%s/job/%s/job/RegisterRepository";
    private static final String SCANNER_JOB_ROOT_URL_PATTERN = "%s/job/RegisterRepository";
    private static final String RESCANNER_JOB_URL_PATTERN = "%s/job/%s/job/%s/job/onPush-%s";
    private static final String RESCANNER_JOB_ROOT_URL_PATTERN = "%s/job/%s/job/onPush-%s";

    private static final Integer HTTP_TIMEOUT = 15;

    private final String url;
    private final String username;
    private final String tokenOrPassword;
    private final String folder;

    public JenkinsIntegrationAdapter(Integration integration) {
        super(integration);

        this.url = getAttributeValue(integration, JenkinsParam.JENKINS_URL);
        this.username = getAttributeValue(integration, JenkinsParam.JENKINS_USERNAME);
        this.tokenOrPassword = getAttributeValue(integration, JenkinsParam.JENKINS_API_TOKEN_OR_PASSWORD);
        this.folder = getAttributeValue(integration, JenkinsParam.JENKINS_FOLDER);
    }

    private enum JenkinsParam implements AdapterParam {
        JENKINS_URL("JENKINS_URL"),
        JENKINS_USERNAME("JENKINS_USER"),
        JENKINS_API_TOKEN_OR_PASSWORD("JENKINS_API_TOKEN_OR_PASSWORD"),
        JENKINS_FOLDER("JENKINS_FOLDER");

        private final String name;

        JenkinsParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public JobResult buildJob(String jobURL, Map<String, String> jobParameters) {
        return buildJobByURL(jobURL, jobParameters);
    }

    @Override
    public void abortJob(String jobURL, Integer buildNumber) {
        abortJobByURL(jobURL, buildNumber);
    }


    public boolean isBuildInProgress(String jobURL, Integer buildNumber) {
        BuildWithDetails details = getBuildWithDetails(jobURL, buildNumber);
        return details.isBuilding();
    }

    @Override
    public String buildLauncherJobUrl() {
        return folder == null || folder.isBlank() ?
                String.format(LAUNCHER_JOB_ROOT_URL_PATTERN, url) :
                String.format(LAUNCHER_JOB_URL_PATTERN, url, folder);
    }

    @Override
    public String buildScannerJobUrl(String repositoryName, boolean rescan) {
        String jobUrl;
        if (rescan) {
            jobUrl = formatReScannerJobUrl(folder, url, repositoryName);
        } else {
            jobUrl = formatScannerJobUrl(folder, url);
        }
        return jobUrl;
    }

    @Override
    public Map<String, String> getBuildParametersMap(String jobURL, Integer buildNumber) {
        BuildWithDetails buildWithDetails = getBuildWithDetails(jobURL, buildNumber);
        return buildWithDetails.getParameters();
    }

    @Override
    public List<BuildParameterType> getBuildParameters(Job ciJob, Integer buildNumber) {
        List jobActions = getJobActions(ciJob.getJobURL(), buildNumber);
        List<BuildParameterType> jobParameters = getJobParameters(jobActions);
        BuildParameterType buildParameter = new BuildParameterType(BuildParameterType.BuildParameterClass.HIDDEN, "ci_run_id", UUID.randomUUID().toString());
        jobParameters.add(buildParameter);
        return jobParameters;
    }

    public Map<Integer, String> getBuildConsoleOutput(Job ciJob, Integer buildNumber, Integer stringsCount, Integer fullCount) {
        BuildWithDetails buildWithDetails = getBuildWithDetails(ciJob.getJobURL(), buildNumber);
        buildWithDetails.isBuilding();
        String consoleOutput = getConsoleOutputHtml(buildWithDetails);
        Map<Integer, String> result = getLastLogStringsByCount(consoleOutput, stringsCount, fullCount);
        if (!buildWithDetails.isBuilding()) {
            result.put(-1, buildWithDetails.getDisplayName());
        }
        return result;
    }

    public Integer getBuildNumber(String queueItemUrl) {
        QueueReference queueReference = new QueueReference(queueItemUrl);
        int attempts = 5;
        long millisToSleep = 2000;
        QueueItem queueItem = null;
        while (attempts > 0) {
            queueItem = getQueueItem(queueReference);
            if (queueItem.getExecutable() != null) {
                break;
            }
            sleep(millisToSleep);
            attempts--;
        }
        Build build = getBuild(queueItem);
        return build.getNumber();
    }

    public Job getJobDetailsFromJenkins(String jobUrl) {
        JobWithDetails jobWithDetails = getJobWithDetails(jobUrl);
        String jobName = JobsService.retrieveJobName(jobWithDetails.getDisplayName());
        return new Job(jobName, jobWithDetails.getUrl().replaceAll("/$", ""));
    }

    private BuildWithDetails getBuildWithDetails(String jobURL, Integer buildNumber) {
        JobWithDetails jobWithDetails = getJobWithDetails(jobURL);
        Build build = jobWithDetails.getBuildByNumber(buildNumber);
        if (build == null) {
            throw new ExternalSystemException(ExternalSystemException.ExternalSystemErrorDetail.JENKINS_BUILD_DOES_NOT_EXIST, String.format(ERR_MSG_UNABLE_TO_GET_BUILD_FROM_JOB, buildNumber, jobURL));
        }
        BuildWithDetails buildWithDetails;
        try {
            buildWithDetails = build.details();
        } catch (IOException e) {
            throw new ExternalSystemException(String.format(ERR_MSG_UNABLE_TO_GET_JOB_BUILD_DETAILS, jobWithDetails.getDisplayName(), build), e);
        }
        return buildWithDetails;
    }

    private JobResult buildJobByURL(String jobURL, Map<String, String> jobParameters) {
        LOGGER.info("JOB_URL: " + jobURL);
        QueueReference reference = buildJobWithParameters(jobURL, jobParameters, true);
        return new JobResult(reference.getQueueItemUrlPart(), true);
    }

    private QueueReference buildJobWithParameters(String jobURL, Map<String, String> jobParameters, boolean crumbFlag) {
        JobWithDetails jobWithDetails = getJobWithDetails(jobURL);
        LOGGER.info("JOB_DISPLAY_NAME: " + jobWithDetails.getDisplayName());
        LOGGER.info("JOB_PARAMETERS: " + jobParameters);
        QueueReference queueReference;
        try {
            queueReference = jobWithDetails.build(jobParameters, crumbFlag);
        } catch (IOException e) {
            throw new ExternalSystemException(String.format(ERR_MSG_UNABLE_TO_BUILD_JOB_WITH_PARAMETERS, jobWithDetails.getDisplayName(), jobParameters), e);
        }
        boolean success = checkReference(queueReference);
        if (!success) {
            throw new ExternalSystemException(ExternalSystemException.ExternalSystemErrorDetail.JENKINS_QUEUE_REFERENCE_IS_NOT_OBTAINED, String.format(ERR_MSG_UNABLE_TO_BUILD_JOB_WITH_PARAMETERS, jobWithDetails.getDisplayName(), jobParameters));
        }
        return queueReference;
    }

    private void abortJobByURL(String jobURL, Integer buildNumber) {
        JobWithDetails job = getJobWithDetails(jobURL);
        QueueReference reference = stop(job, buildNumber);
        boolean success = checkReference(reference);
        if (!success) {
            reference = terminate(job, buildNumber);
            success = checkReference(reference);
        }
        if (!success) {
            throw new ExternalSystemException(ExternalSystemException.ExternalSystemErrorDetail.JENKINS_QUEUE_REFERENCE_IS_NOT_OBTAINED, String.format(ERR_MSG_UNABLE_TO_ABORT_JOB, jobURL));
        }
    }

    private QueueReference stop(JobWithDetails job, Integer buildNumber) {
        ExtractHeader location;
        try {
            location = job.getClient().post(job.getUrl() + buildNumber + "/stop", null, ExtractHeader.class);
        } catch (IOException e) {
            throw new ExternalSystemException(String.format(ERR_MSG_UNABLE_TO_STOP_JOB, job.getDisplayName()), e);
        }
        return new QueueReference(location.getLocation());
    }

    private QueueReference terminate(JobWithDetails job, Integer buildNumber) {
        ExtractHeader location;
        try {
            location = job.getClient().post(job.getUrl() + buildNumber + "/term", null, ExtractHeader.class);
        } catch (IOException e) {
            throw new ExternalSystemException(String.format(ERR_MSG_UNABLE_TO_TERMINATE_JOB, job.getDisplayName()), e);
        }
        return new QueueReference(location.getLocation());
    }

    private boolean checkReference(QueueReference reference) {
        return reference != null && !StringUtils.isEmpty(reference.getQueueItemUrlPart());
    }

    private List getJobActions(String jobURL, Integer buildNumber) {
        BuildWithDetails buildWithDetails = getBuildWithDetails(jobURL, buildNumber);
        return buildWithDetails.getActions();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<BuildParameterType> getJobParameters(List actions) {
        Collection parameters = Collections2.filter(actions,
                (Predicate<Map<String, Object>>) action -> action.containsKey("parameters"));
        List<BuildParameterType> params = new ArrayList<>();
        if (parameters != null && !parameters.isEmpty()) {
            for (Object o : ((List) ((Map) parameters.toArray()[0]).get("parameters"))) {
                BuildParameterType buildParameter = new BuildParameterType();
                Map<String, Object> param = (Map) o;
                String name = String.valueOf(param.get("name"));
                String value = String.valueOf(param.get("value"));
                String buildParamClass = String.valueOf(param.get("_class"));
                buildParameter.setName(name);
                buildParameter.setValue(value);
                if (buildParamClass.contains("Hide")) {
                    buildParameter.setParameterClass(BuildParameterType.BuildParameterClass.HIDDEN);
                } else if (buildParamClass.contains("String")) {
                    buildParameter.setParameterClass(BuildParameterType.BuildParameterClass.STRING);
                } else if (buildParamClass.contains("Boolean")) {
                    buildParameter.setParameterClass(BuildParameterType.BuildParameterClass.BOOLEAN);
                }
                if (!name.equals("ci_run_id"))
                    params.add(buildParameter);
            }
        }
        return params;
    }

    private String getConsoleOutputHtml(BuildWithDetails buildWithDetails) {
        String consoleOutput;
        try {
            consoleOutput = buildWithDetails.getConsoleOutputHtml();
        } catch (IOException e) {
            throw new ExternalSystemException(String.format(ERR_MSG_UNABLE_TO_GET_CONSOLE_OUTPUT_FROM_BUILD, buildWithDetails.getDisplayName()), e);
        }
        return consoleOutput;
    }

    private QueueItem getQueueItem(QueueReference queueReference) {
        QueueItem queueItem;
        try {
            queueItem = getJenkinsServer().getQueueItem(queueReference);
        } catch (IOException e) {
            throw new ExternalSystemException(String.format(ERR_MSG_UNABLE_TO_GET_QUEUE_ITEM_BY_REFERENCE, queueReference), e);
        }
        return queueItem;
    }

    private Build getBuild(QueueItem queueItem) {
        try {
            return getJenkinsServer().getBuild(queueItem);
        } catch (IOException e) {
            throw new ExternalSystemException(String.format(ERR_MSG_UNABLE_TO_GET_BUILD_OBJECT_BY_QUEUE_ITEM, queueItem), e);
        }
    }

    private JobWithDetails getJobWithDetails(String jobURL) {
        FolderJob folderJob = getFolderJobFromURL(jobURL);
        String jobName = getJobNameFromURL(jobURL);

        JobWithDetails jobWithDetails = getJobByFolderAndName(folderJob, jobName);
        if (jobWithDetails == null) {
            throw new ExternalSystemException(ExternalSystemException.ExternalSystemErrorDetail.JENKINS_JOB_DOES_NOT_EXIST, String.format(ERR_MSG_UNABLE_TO_GET_JOB_BY_URL, jobURL));
        }
        return jobWithDetails;
    }

    private String getJobNameFromURL(String jobURL) {
        Path path = Paths.get(jobURL);
        return path.getName(path.getNameCount() - 1).toString();
    }

    private FolderJob getFolderJobFromURL(String jobUrl) {
        Path path = Paths.get(jobUrl);
        // Extracts folder name
        String folderName = path.getName(path.getNameCount() - 3).toString();
        // Extracts folder url
        String folderUrl = jobUrl.substring(0, jobUrl.lastIndexOf("job/"));
        return new FolderJob(folderName, folderUrl);
    }

    private JobWithDetails getJobByFolderAndName(FolderJob folderJob, String jobName) {
        try {
            return getJenkinsServer().getJob(folderJob, jobName);
        } catch (IOException e) {
            throw new ExternalSystemException(String.format(ERR_MSG_UNABLE_TO_GET_JOB_FROM_FOLDER, jobName, folderJob.getUrl()), e);
        }
    }

    private Map<Integer, String> getLastLogStringsByCount(String log, Integer count, Integer fullCount) {
        Map<Integer, String> logMap = new HashMap<>();
        int zero = 0;
        String[] strings = log.split("\n");
        count = strings.length < count ? strings.length : count;
        if (fullCount != zero) {
            count = strings.length > fullCount ? strings.length - fullCount : zero;
        }
        logMap.put(strings.length,
                String.join("\n", Arrays.copyOfRange(strings, strings.length - count, strings.length)));
        return logMap;
    }

    private String formatReScannerJobUrl(String jenkinsFolder, String jenkinsHost, String repositoryName) {
        String reScannerJobUrl;
        if (StringUtils.isEmpty(jenkinsFolder)) {
            reScannerJobUrl = String.format(RESCANNER_JOB_ROOT_URL_PATTERN, jenkinsHost, repositoryName, repositoryName);
        } else {
            reScannerJobUrl = String.format(RESCANNER_JOB_URL_PATTERN, jenkinsHost, jenkinsFolder, repositoryName, repositoryName);
        }
        return reScannerJobUrl;
    }

    private String formatScannerJobUrl(String jenkinsFolder, String jenkinsHost) {
        String scannerJobUrl;
        if (StringUtils.isEmpty(jenkinsFolder)) {
            scannerJobUrl = String.format(SCANNER_JOB_ROOT_URL_PATTERN, jenkinsHost);
        } else {
            scannerJobUrl = String.format(SCANNER_JOB_URL_PATTERN, jenkinsHost, jenkinsFolder);
        }
        return scannerJobUrl;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return getJenkinsServer().isRunning();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getTokenOrPassword() {
        return tokenOrPassword;
    }

    @Override
    public String getFolder() {
        return folder;
    }

    public JenkinsServer getJenkinsServer() {
        JenkinsServer jenkinsServer;
        try {
            JenkinsConfig config = new JenkinsConfig(username, tokenOrPassword, HTTP_TIMEOUT);
            jenkinsServer = new JenkinsServer(new JenkinsClient(new URI(url), config));
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IntegrationException(IntegrationException.IntegrationExceptionDetail.JENKINS_SERVER_INITIALIZATION_FAILED, e.getMessage(), e);
        }
        return jenkinsServer;
    }
}
