package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.dto.TestRunType;
import com.zebrunner.reporting.service.LauncherCallbackService;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("Launcher callbacks API")
@CrossOrigin
@RequestMapping(path = "api/launcher-callbacks", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class LauncherCallbackAPIController extends AbstractController {

    private final LauncherCallbackService launcherCallbackService;
    private final Mapper mapper;

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves test run by launcher callback reference token", nickname = "getInfo", httpMethod = "GET", response = TestRunType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/{ref}")
    public TestRunType getInfo(@PathVariable("ref") String ref) {
        TestRun testRun = launcherCallbackService.buildInfo(ref);
        return mapper.map(testRun, TestRunType.class);
    }
}
