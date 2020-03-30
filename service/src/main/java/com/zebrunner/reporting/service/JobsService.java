package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.JobMapper;
import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.exception.ProcessingException;
import com.zebrunner.reporting.service.integration.IntegrationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.zebrunner.reporting.service.exception.ProcessingException.ProcessingErrorDetail.UNPROCESSABLE_URL;

@Service
public class JobsService {

    private static final String  INTEGRATION_TYPE_NAME = "JENKINS";

    private static final String ERR_MSG_UNABLE_TO_PARSE_URI = "Provided uri '%s' is malformed";

    private final JobMapper jobMapper;
    private final IntegrationService integrationService;
    private final UserService userService;

    public JobsService(JobMapper jobMapper, IntegrationService integrationService, UserService userService) {
        this.jobMapper = jobMapper;
        this.integrationService = integrationService;
        this.userService = userService;
    }

    @Transactional
    public void createJob(Job job) {
        jobMapper.createJob(job);
    }

    // Check the same logic in ZafiraClient method registerJob
    @Transactional
    public Job createOrUpdateJobByURL(String jobUrl, Long userId) {
        Job job = createJobFromURL(jobUrl, userId);
        return createOrUpdateJob(job);
    }

    private Job createJobFromURL(String jobUrl, Long userId) {
        User user = userService.getUserById(userId);
        // Replacing trailing slash we make sure that further operations
        // based on splitting by slash will be performed correctly
        jobUrl = jobUrl.replaceAll("/$", "");
        String jobName = retrieveJobName(jobUrl);
        String jenkinsHost = parseJenkinsHost(jobUrl);
        return new Job(jobName, jobUrl, jenkinsHost, user);
    }

    public static String retrieveJobName(String jobUrl) {
        try {
            String decodedJobUrl = URLDecoder.decode(jobUrl, StandardCharsets.UTF_8.toString());
            return StringUtils.substringAfterLast(decodedJobUrl, "/");
        } catch (UnsupportedEncodingException e) {
            throw new ProcessingException(UNPROCESSABLE_URL, String.format(ERR_MSG_UNABLE_TO_PARSE_URI, jobUrl));
        }
    }

    private String parseJenkinsHost(String jobUrl) {
        String jenkinsHost = StringUtils.EMPTY;
        if (jobUrl.contains("/view/")) {
            jenkinsHost = jobUrl.split("/view/")[0];
        } else if (jobUrl.contains("/job/")) {
            jenkinsHost = jobUrl.split("/job/")[0];
        }
        return jenkinsHost;
    }

    @Transactional(readOnly = true)
    public List<Job> getAllJobs() {
        return jobMapper.getAllJobs();
    }

    @Transactional(readOnly = true)
    public Job getJobByJobURL(String url) {
        return jobMapper.getJobByJobURL(url);
    }

    @Transactional
    public Job updateJob(Job job) {
        jobMapper.updateJob(job);
        return job;
    }

    @Transactional
    public Job createOrUpdateJob(Job newJob) {
        Integration integration = integrationService.retrieveByJobAndIntegrationTypeName(newJob, INTEGRATION_TYPE_NAME);
        newJob.setAutomationServerId(integration.getId());
        Job job = getJobByJobURL(newJob.getJobURL());
        if (job == null) {
            createJob(newJob);
        } else if (!job.equals(newJob)) {
            newJob.setId(job.getId());
            updateJob(newJob);
        } else {
            newJob = job;
        }
        return newJob;
    }
}
