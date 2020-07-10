package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.launcher.LauncherPreset;
import com.zebrunner.reporting.domain.dto.LauncherPresetDTO;
import com.zebrunner.reporting.service.LauncherPresetService;
import com.zebrunner.reporting.web.documented.LauncherPresetDocumentedController;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RequestMapping(path = "api/launchers/{launcherId}/presets", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class LauncherPresetAPIController extends AbstractController implements LauncherPresetDocumentedController {

    private final LauncherPresetService launcherPresetService;
    private final Mapper mapper;

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping()
    @Override
    public LauncherPresetDTO createLauncherPreset(@RequestBody @Valid LauncherPresetDTO launcherPresetDTO, @PathVariable("launcherId") Long launcherId) {
        LauncherPreset launcherPreset = mapper.map(launcherPresetDTO, LauncherPreset.class);
        launcherPreset = launcherPresetService.create(launcherPreset, launcherId);
        launcherPresetDTO = mapper.map(launcherPreset, LauncherPresetDTO.class);
        return launcherPresetDTO;
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @GetMapping(value = "/{id}/hook", produces = MediaType.TEXT_PLAIN_VALUE)
    @Override
    public String buildWebHookUrl(@PathVariable("id") Long id) {
        return launcherPresetService.buildWebHookUrl(id);
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/{id}/hook/{ref}")
    @Override
    public void revokeReference(
            @PathVariable("id") Long id,
            @PathVariable("ref") String ref,
            @PathVariable("launcherId") Long launcherId
    ) {
        launcherPresetService.revokeReference(id, ref, launcherId);
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PutMapping("/{id}")
    @Override
    public LauncherPresetDTO updateLauncherPreset(@RequestBody @Valid LauncherPresetDTO launcherPresetDTO, @PathVariable("id") Long id, @PathVariable("launcherId") Long launcherId) {
        LauncherPreset launcherPreset = mapper.map(launcherPresetDTO, LauncherPreset.class);
        launcherPreset.setId(id);
        launcherPreset = launcherPresetService.update(launcherPreset, launcherId);
        return mapper.map(launcherPreset, LauncherPresetDTO.class);
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/{id}")
    @Override
    public void deleteLauncherPreset(@PathVariable("id") Long id, @PathVariable("launcherId") Long launcherId) {
        launcherPresetService.deleteByIdAndLauncherId(id, launcherId);
    }

}
