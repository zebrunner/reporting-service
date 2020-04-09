package com.zebrunner.reporting.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zebrunner.reporting.domain.db.launcher.UserLauncherPreference;
import com.zebrunner.reporting.persistence.dao.mysql.application.LauncherMapper;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.domain.db.JenkinsJob;
import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.db.launcher.Launcher;
import com.zebrunner.reporting.domain.db.launcher.LauncherCallback;
import com.zebrunner.reporting.domain.db.launcher.LauncherPreset;
import com.zebrunner.reporting.domain.db.ScmAccount;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.dto.JobResult;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.tool.impl.AutomationServerService;
import com.zebrunner.reporting.service.integration.tool.impl.TestAutomationToolService;
import com.zebrunner.reporting.service.scm.GitHubService;
import com.zebrunner.reporting.service.scm.ScmAccountService;
import com.zebrunner.reporting.service.util.URLResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.JOB_CAN_NOT_BE_STARTED;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.LAUNCHER_NOT_FOUND;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.USER_NOT_FOUND;

@Service
public class LauncherService {

    private static final String ERR_MSG_NO_BUILD_LAUNCHER_JOB_SPECIFIED = "No launcher job specified";
    private static final String ERR_MSG_REQUIRED_JOB_ARGUMENTS_NOT_FOUND = "Required job arguments not found";
    private static final String ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST = "User with id %d doesn't exist";

    private static final Set<String> MANDATORY_ARGUMENTS = Set.of("scmURL", "branch", "zafiraFields");

    private final LauncherMapper launcherMapper;
    private final LauncherPresetService launcherPresetService;
    private final LauncherCallbackService launcherCallbackService;
    private final UserLauncherPreferenceService preferenceService;
    private final AutomationServerService automationServerService;
    private final ScmAccountService scmAccountService;
    private final JobsService jobsService;
    private final JWTService jwtService;
    private final GitHubService gitHubService;
    private final TestAutomationToolService testAutomationToolService;
    private final CryptoService cryptoService;
    private final URLResolver urlResolver;
    private final UserService userService;

    public LauncherService(
            LauncherMapper launcherMapper,
            LauncherPresetService launcherPresetService,
            LauncherCallbackService launcherCallbackService,
            UserLauncherPreferenceService preferenceService,
            AutomationServerService automationServerService,
            ScmAccountService scmAccountService,
            JobsService jobsService,
            JWTService jwtService,
            GitHubService gitHubService,
            TestAutomationToolService testAutomationToolService,
            CryptoService cryptoService,
            URLResolver urlResolver,
            UserService userService
    ) {
        this.launcherMapper = launcherMapper;
        this.launcherPresetService = launcherPresetService;
        this.launcherCallbackService = launcherCallbackService;
        this.preferenceService = preferenceService;
        this.automationServerService = automationServerService;
        this.scmAccountService = scmAccountService;
        this.jobsService = jobsService;
        this.jwtService = jwtService;
        this.gitHubService = gitHubService;
        this.testAutomationToolService = testAutomationToolService;
        this.cryptoService = cryptoService;
        this.urlResolver = urlResolver;
        this.userService = userService;
    }

    @Transactional
    public Launcher createLauncher(Launcher launcher, Long userId, Long automationServerId) {
        launcher.setAutoScan(false);
        if (automationServerService.isEnabledAndConnected(automationServerId)) {
            String launcherJobUrl = automationServerService.buildLauncherJobUrl(automationServerId);
            // Checks whether job is present om Jenkins. If it is not, exception will be thrown.
            automationServerService.getJobByUrl(launcherJobUrl, automationServerId);
            Job job = jobsService.createOrUpdateJobByURL(launcherJobUrl, userId);
            launcher.setJob(job);
        }
        launcherMapper.createLauncher(launcher);

        if (launcher.getPreference() != null) {
            UserLauncherPreference preference = preferenceService.create(launcher.getId(), userId, launcher.getPreference());
            launcher.setPreference(preference);
        }
        return launcher;
    }

    @Transactional
    public List<Launcher> mergeLaunchersWithJenkinsJobs(List<JenkinsJob> jenkinsJobs, String repo, boolean isSuccess, long userId) {
        if (!isSuccess) {
            return new ArrayList<>();
        }
        ScmAccount scmAccount = scmAccountService.getScmAccountByRepo(repo);
        List<Launcher> launchers = getAutoScannedByScmAccountId(scmAccount.getId());
        Map<Launcher, JenkinsJob> launchersToMerge = collectLaunchersToMerge(launchers, jenkinsJobs);
        List<JenkinsJob> jenkinsJobsToCreate = collectJenkinsJobsToCreate(launchers, jenkinsJobs);
        List<Long> launcherIdsToDelete = launchers.stream()
                                                  .filter(launcher -> !launchersToMerge.containsKey(launcher))
                                                  .map(Launcher::getId)
                                                  .collect(Collectors.toList());

        List<Launcher> mergedLaunchers = mergeLaunchers(launchersToMerge);
        mergedLaunchers.addAll(
                batchCreateFromJenkinsJobs(jenkinsJobsToCreate, scmAccount, userId)
        );
        batchDelete(launcherIdsToDelete);
        return mergedLaunchers;
    }

    private Map<Launcher, JenkinsJob> collectLaunchersToMerge(List<Launcher> launchers, List<JenkinsJob> jenkinsJobs) {
        return launchers.stream()
                        .map(launcher -> jenkinsJobs.stream()
                                                    .filter(jenkinsJob -> isLauncherEqualJenkinsJob(launcher, jenkinsJob))
                                                    .findFirst()
                                                    .map(jenkinsJob -> new AbstractMap.SimpleEntry<>(launcher, jenkinsJob)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    private List<JenkinsJob> collectJenkinsJobsToCreate(List<Launcher> launchers, List<JenkinsJob> jenkinsJobs) {
        return jenkinsJobs.stream()
                          .filter(jenkinsJob -> launchers.stream()
                                                         .filter(launcher -> isLauncherEqualJenkinsJob(launcher, jenkinsJob))
                                                         .findFirst()
                                                         .isEmpty())
                          .collect(Collectors.toList());
    }

    private boolean isLauncherEqualJenkinsJob(Launcher launcher, JenkinsJob jenkinsJob) {
        return jenkinsJob.getUrl().equals(launcher.getJob().getJobURL());
    }

    private List<Launcher> mergeLaunchers(Map<Launcher, JenkinsJob> launchersToMerge) {
        List<Launcher> launchers = launchersToMerge.entrySet().stream()
                                                   .map(entry -> mergeLauncherWithJob(entry.getKey(), entry.getValue()))
                                                   .collect(Collectors.toList());
        return batchUpdate(launchers);
    }

    private Launcher mergeLauncherWithJob(Launcher launcher, JenkinsJob jenkinsJob) {
        launcher.setModel(jenkinsJob.getParameters());
        launcher.setType(jenkinsJob.getType());
        return launcher;
    }

    @Transactional
    public List<Launcher> batchCreateFromJenkinsJobs(List<JenkinsJob> jenkinsJobs, ScmAccount scmAccount, Long userId) {
        List<Launcher> launchersToCreate = jenkinsJobs.stream()
                                                      .map(jenkinsJob -> {
                                                          String jobUrl = jenkinsJob.getUrl();
                                                          Job job = jobsService.createOrUpdateJobByURL(jobUrl, userId);
                                                          return new Launcher(job.getName(), jenkinsJob.getParameters(), scmAccount, job, jenkinsJob.getType(), true);
                                                      })
                                                      .collect(Collectors.toList());
        return batchCreate(launchersToCreate);
    }

    @Transactional
    public List<Launcher> batchCreate(List<Launcher> launchers) {
        launcherMapper.batchCreate(launchers);
        return launchers;
    }

    private Launcher createLauncherForJenkinsJob(long userId, ScmAccount scmAccount, JenkinsJob jenkinsJob) {
        String jobUrl = jenkinsJob.getUrl();
        Job job = jobsService.createOrUpdateJobByURL(jobUrl, userId);
        Launcher launcher = new Launcher(job.getName(), jenkinsJob.getParameters(), scmAccount, job, jenkinsJob.getType(), true);
        launcherMapper.createLauncher(launcher);
        return launcher;
    }

    @Transactional(readOnly = true)
    public List<Launcher> getAutoScannedByScmAccountId(Long scmAccountId) {
        return launcherMapper.getAllAutoScannedByScmAccountId(scmAccountId);
    }

    @Transactional(readOnly = true)
    public Launcher getLauncherById(Long id) {
        Launcher launcher = launcherMapper.getLauncherById(id);
        if (launcher == null) {
            throw new ResourceNotFoundException(LAUNCHER_NOT_FOUND, String.format("Unable to locate launcher with id '%d'", id));
        }
        return launcher;
    }

    @Transactional(readOnly = true)
    public List<Launcher> getAllLaunchers(Long userId) {
        return launcherMapper.getAllLaunchers(userId);
    }

    @Transactional(readOnly = true)
    public Launcher retrieveByPresetReference(String ref) {
        Launcher launcher = launcherMapper.getByPresetReference(ref);
        if (launcher == null) {
            throw new ResourceNotFoundException(LAUNCHER_NOT_FOUND, String.format("Unable to locate launcher with preset reference '%s'", ref));
        }
        return launcher;
    }

    @Transactional
    public Launcher updateLauncher(Launcher launcher) {
        launcherMapper.updateLauncher(launcher);
        return launcher;
    }

    @Transactional
    public List<Launcher> batchUpdate(List<Launcher> launchers) {
        launcherMapper.batchUpdate(launchers);
        return launchers;
    }

    @Transactional
    public void deleteLauncherById(Long id) {
        launcherMapper.deleteLauncherById(id);
    }

    @Transactional
    public void batchDelete(List<Long> ids) {
        launcherMapper.batchDelete(ids);
    }

    @Transactional(readOnly = true)
    public String buildLauncherJob(Launcher launcher, Long userId, Long providerId) throws IOException {
        User user = userService.getNotNullUserById(userId);
        Long scmAccountId = launcher.getScmAccount().getId();
        ScmAccount scmAccount = scmAccountService.getScmAccountById(scmAccountId);
        Job job = launcher.getJob();
        if (job == null) {
            throw new IllegalOperationException(JOB_CAN_NOT_BE_STARTED, ERR_MSG_NO_BUILD_LAUNCHER_JOB_SPECIFIED);
        }
        // CiRunId is a random string, needs to define unique correlation between started launcher and real test run starting
        // It must be returned with test run on start in testRun.ciRunId field
        String ciRunId = UUID.randomUUID().toString();

        Map<String, String> jobParameters = buildLauncherJobParametersMap(launcher, user, scmAccount, ciRunId, providerId);
        automationServerService.buildJob(job, jobParameters);

        return ciRunId;
    }

    private Map<String, String> buildLauncherJobParametersMap(Launcher launcher, User user, ScmAccount scmAccount, String ciRunId, Long providerId) throws IOException {
        Map<String, String> jobParameters = new ObjectMapper().readValue(launcher.getModel(), new TypeReference<Map<String, String>>() {});

        String decryptedAccessToken = cryptoService.decrypt(scmAccount.getAccessToken());
        String authorizedURL = scmAccount.buildAuthorizedURL(decryptedAccessToken);
        jobParameters.put("scmURL", authorizedURL);

        if (!jobParameters.containsKey("branch")) {
            jobParameters.put("branch", "*/master");
        }

        // check if selected integration is enabled. Provider id can be null for API tests
        if (providerId != null && testAutomationToolService.isEnabledAndConnected(providerId)) {
            providerId = launcherPresetService.getTestEnvironmentProviderId(providerId);
            String testExecutorHost = testAutomationToolService.buildUrl(providerId);
            // param name is confusing but all automation tools follow the same contract so it will actually work
            jobParameters.put("selenium_url", testExecutorHost);
        }

        jobParameters.put("zafira_enabled", "true");
        jobParameters.put("zafira_service_url", urlResolver.buildWebserviceUrl());
        jobParameters.put("zafira_access_token", jwtService.generateAccessToken(user, TenancyContext.getTenantName()));

        String args = jobParameters.entrySet().stream()
                                   .filter(param -> !MANDATORY_ARGUMENTS.contains(param.getKey()))
                                   .map(param -> param.getKey() + "=" + param.getValue())
                                   .collect(Collectors.joining(","));

        jobParameters.put("zafiraFields", args);
        jobParameters.put("ci_run_id", ciRunId);

        if (!jobParameters.keySet().containsAll(MANDATORY_ARGUMENTS)) {
            throw new IllegalOperationException(JOB_CAN_NOT_BE_STARTED, ERR_MSG_REQUIRED_JOB_ARGUMENTS_NOT_FOUND);
        }

        return jobParameters;
    }

    @Transactional
    public String buildLauncherJobByPresetRef(String ref, String callbackUrl, Long userId, Long providerId) throws IOException {
        if (userId == 0) {
            User anonymous = userService.getDefaultUser();
            userId = anonymous.getId();
        }
        Launcher launcher = retrieveByPresetReference(ref);
        LauncherPreset preset = launcherPresetService.retrieveByRef(ref);
        launcher.setModel(preset.getParams());
        String ciRunId = buildLauncherJob(launcher, userId, providerId);

        LauncherCallback callback = null;
        if (callbackUrl != null) {
            callback = new LauncherCallback(ciRunId, callbackUrl, preset);
            launcherCallbackService.create(callback);
        }

        return callback != null ? callback.getRef() : null;
    }

    @Transactional(readOnly = true)
    public JobResult buildScannerJob(Long userId, String branch, long scmAccountId, boolean rescan, Long automationServerId) {
        ScmAccount scmAccount = scmAccountService.getScmAccountById(scmAccountId);
        User user = userService.getNotNullUserById(userId);
        Map<String, String> jobParameters = buildScannerJobParametersMap(automationServerId, user, branch, scmAccount);
        return automationServerService.buildScannerJob(scmAccount.getRepositoryName(), jobParameters, rescan, automationServerId);
    }

    private Map<String, String> buildScannerJobParametersMap(Long automationServerId, User user, String branch, ScmAccount scmAccount) {
        Map<String, String> jobParameters = new HashMap<>();

        String organizationName = scmAccount.getOrganizationName();
        String repositoryName = scmAccount.getRepositoryName();
        String scmUser = gitHubService.getLoginName(scmAccount);
        String scmToken = cryptoService.decrypt(scmAccount.getAccessToken());
        String serviceUrl = urlResolver.buildWebserviceUrl();
        String accessToken = jwtService.generateAccessToken(user, TenancyContext.getTenantName());

        jobParameters.put("userId", String.valueOf(user.getId()));
        if (StringUtils.isNotEmpty(automationServerService.getFolder(automationServerId))) {
            jobParameters.put("scmOrg", organizationName);
        }
        jobParameters.put("repo", repositoryName);
        jobParameters.put("branch", branch);
        jobParameters.put("scmUser", scmUser);
        jobParameters.put("scmToken", scmToken);
        jobParameters.put("zafira_service_url", serviceUrl);
        jobParameters.put("zafira_access_token", accessToken);
        jobParameters.put("onlyUpdated", String.valueOf(false));

        String args = jobParameters.entrySet().stream()
                                   .map(param -> param.getKey() + "=" + param.getValue()).collect(Collectors.joining(","));

        jobParameters.put("zafiraFields", args);

        return jobParameters;
    }

    @Transactional(readOnly = true)
    public void abortScannerJob(long scmAccountId, Integer buildNumber, boolean rescan, Long automationServerId) {
        ScmAccount scmAccount = scmAccountService.getScmAccountById(scmAccountId);
        String repositoryName = scmAccount.getRepositoryName();
        automationServerService.abortScannerJob(repositoryName, buildNumber, rescan, automationServerId);
    }

    public Integer getBuildNumber(String queueItemUrl, Long automationServerId) {
        return automationServerService.getBuildNumber(queueItemUrl, automationServerId);
    }

    @Transactional(readOnly = true)
    public boolean isExistById(Long id) {
        return launcherMapper.isExistById(id);
    }

    @Transactional
    public UserLauncherPreference markLauncherAsFavorite(Long id, Long userId, boolean favorite) {
        UserLauncherPreference preference;

        boolean launcherExists = isExistById(id);
        if (!launcherExists) {
            throw new ResourceNotFoundException(LAUNCHER_NOT_FOUND, String.format("Unable to locate launcher with id '%d'", id));
        }

        boolean userExists = userService.isExistById(userId);
        if (!userExists) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST, id);
        }

        boolean userLauncherPreferenceExists = preferenceService.isExistByLauncherIdAndUserId(id, userId);
        if (userLauncherPreferenceExists) {
            UserLauncherPreference dbPreference = preferenceService.retrieveByLauncherIdAndUserId(id, userId);
            dbPreference.setFavorite(favorite);
            preference = preferenceService.update(dbPreference);
        } else {
            preference = new UserLauncherPreference(favorite);
            preference = preferenceService.create(id, userId, preference);
        }
        return preference;
    }

}
