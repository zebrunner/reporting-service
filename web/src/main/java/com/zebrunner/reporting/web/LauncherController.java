package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.JenkinsJob;
import com.zebrunner.reporting.domain.db.launcher.Launcher;
import com.zebrunner.reporting.domain.db.launcher.UserLauncherPreference;
import com.zebrunner.reporting.domain.dto.JenkinsJobsScanResultDTO;
import com.zebrunner.reporting.domain.dto.JobResult;
import com.zebrunner.reporting.domain.dto.LauncherDTO;
import com.zebrunner.reporting.domain.dto.LauncherScannerType;
import com.zebrunner.reporting.domain.push.LauncherPush;
import com.zebrunner.reporting.domain.push.LauncherRunPush;
import com.zebrunner.reporting.service.LauncherService;
import com.zebrunner.reporting.web.documented.LauncherDocumentedController;
import com.zebrunner.reporting.web.util.patch.PatchDecorator;
import com.zebrunner.reporting.web.util.patch.PatchDescriptor;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RequestMapping(path = "api/launchers", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class LauncherController extends AbstractController implements LauncherDocumentedController {

    private final LauncherService launcherService;
    private final Mapper mapper;
    private final SimpMessagingTemplate websocketTemplate;

    public LauncherController(LauncherService launcherService, Mapper mapper, SimpMessagingTemplate websocketTemplate) {
        this.launcherService = launcherService;
        this.mapper = mapper;
        this.websocketTemplate = websocketTemplate;
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping()
    @Override
    public LauncherDTO createLauncher(@RequestBody @Valid LauncherDTO launcherDTO,
                                      @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        Launcher launcher = mapper.map(launcherDTO, Launcher.class);
        Long principalId = getPrincipalId();
        launcher = launcherService.createLauncher(launcher, principalId, automationServerId);
        return mapper.map(launcher, LauncherDTO.class);
    }

    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/{id}")
    @Override
    public LauncherDTO getLauncherById(@PathVariable("id") Long id) {
        Launcher launcher = launcherService.getLauncherById(id);
        return mapper.map(launcher, LauncherDTO.class);
    }

    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping()
    @Override
    public List<LauncherDTO> getAllLaunchers() {
        List<Launcher> launchers = launcherService.getAllLaunchers(getPrincipalId());
        return launchers.stream()
                        .map(launcher -> mapper.map(launcher, LauncherDTO.class))
                        .collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PutMapping()
    @Override
    public LauncherDTO updateLauncher(@RequestBody @Valid LauncherDTO launcherDTO) {
        Launcher launcher = mapper.map(launcherDTO, Launcher.class);
        launcher = launcherService.updateLauncher(launcher);
        return mapper.map(launcher, LauncherDTO.class);
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/{id}")
    @Override
    public void deleteLauncherById(@PathVariable("id") Long id) {
        launcherService.deleteLauncherById(id);
    }

    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @PostMapping("/build")
    @Override
    public void build(@RequestBody @Valid LauncherDTO launcherDTO,
                      @RequestParam(name = "providerId", required = false) Long providerId) throws IOException {
        Launcher launcher = mapper.map(launcherDTO, Launcher.class);
        String ciRunId = launcherService.buildLauncherJob(launcher, getPrincipalId(), providerId);
        websocketTemplate.convertAndSend(getLauncherRunsWebsocketPath(), new LauncherRunPush(launcher, ciRunId));
    }

    @GetMapping("/hooks/{ref}")
    @Override
    public String buildByWebHook(
        @PathVariable("ref") String ref,
        @RequestParam(value = "callbackUrl", required = false) String callbackUrl,
        @RequestParam(name = "providerId", required = false) Long providerId
    ) throws IOException {
        return launcherService.buildLauncherJobByPresetRef(ref, callbackUrl, getPrincipalId(), providerId);
    }

    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/build/number")
    @Override
    public Integer getBuildNumber(@RequestParam("queueItemUrl") String queueItemUrl,
                                  @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        return launcherService.getBuildNumber(queueItemUrl, automationServerId);
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping("/scanner")
    @Override
    public JobResult runScanner(@RequestBody @Valid LauncherScannerType launcherScannerType,
                                @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        return launcherService.buildScannerJob(
                getPrincipalId(),
                launcherScannerType.getBranch(),
                launcherScannerType.getScmAccountId(),
                launcherScannerType.isRescan(),
                automationServerId
        );
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/scanner/{buildNumber}")
    @Override
    public void cancelScanner(@PathVariable("buildNumber") int buildNumber,
                              @RequestParam("scmAccountId") Long scmAccountId,
                              @RequestParam("rescan") boolean rescan,
                              @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        launcherService.abortScannerJob(scmAccountId, buildNumber, rescan, automationServerId);
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/scanner/{buildNumber}/status")
    public boolean getScannerStatus(@PathVariable("buildNumber") int buildNumber,
                                    @RequestParam("scmAccountId") Long scmAccountId,
                                    @RequestParam("rescan") boolean rescan,
                                    @RequestParam(name = "automationServerId", required = false) Long automationServerId) {
        return launcherService.isScannerJobInProgress(scmAccountId, buildNumber, rescan, automationServerId);
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping("/create")
    @Override
    public List<LauncherDTO> scanLaunchersFromJenkins(@RequestBody @Valid JenkinsJobsScanResultDTO jenkinsJobsScanResultDTO) {
        Long principalId = getPrincipalId();
        List<JenkinsJob> jenkinsJobs = jenkinsJobsScanResultDTO.getJenkinsJobs()
                                                               .stream()
                                                               .map(jenkinsLauncher -> mapper.map(jenkinsLauncher, JenkinsJob.class))
                                                               .collect(Collectors.toList());
        List<Launcher> launchers = launcherService.mergeLaunchersWithJenkinsJobs(jenkinsJobs, jenkinsJobsScanResultDTO.getRepo(), jenkinsJobsScanResultDTO.isSuccess(), principalId);
        List<LauncherDTO> launcherDTOS = launchers.stream()
                                                  .map(launcher -> mapper.map(launcher, LauncherDTO.class))
                                                  .collect(Collectors.toList());
        websocketTemplate.convertAndSend(getLaunchersWebsocketPath(), new LauncherPush(launcherDTOS, jenkinsJobsScanResultDTO.getUserId(), jenkinsJobsScanResultDTO.isSuccess()));
        return launcherDTOS;
    }

    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @PatchMapping("/{id}")
    @Override
    public UserLauncherPreference patchUserLauncherPreference(@RequestBody @Valid PatchDescriptor descriptor, @PathVariable("id") Long id) {
        return PatchDecorator.<UserLauncherPreference, Boolean>descriptor(descriptor)
                .operation(PatchOperation.class)

                .when(PatchOperation.SAVE_FAVORITE)
                .withParameter(Boolean::valueOf)
                .then(favorite -> launcherService.markLauncherAsFavorite(id, getPrincipalId(), favorite))

                .after()
                .decorate();
    }

    enum PatchOperation {
        SAVE_FAVORITE
    }

}
