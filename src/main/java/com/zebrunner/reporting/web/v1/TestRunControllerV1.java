package com.zebrunner.reporting.web.v1;

import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.reporting.Test;
import com.zebrunner.reporting.domain.db.reporting.TestRun;
import com.zebrunner.reporting.domain.dto.TestRunStatistics;
import com.zebrunner.reporting.domain.push.TestPush;
import com.zebrunner.reporting.domain.push.TestRunPush;
import com.zebrunner.reporting.domain.push.TestRunStatisticPush;
import com.zebrunner.reporting.service.LauncherCallbackService;
import com.zebrunner.reporting.service.cache.TestRunStatisticsCacheableService;
import com.zebrunner.reporting.service.reporting.TestRunServiceV1;
import com.zebrunner.reporting.web.AbstractController;
import com.zebrunner.reporting.web.request.v1.HeadlessTestStartRequest;
import com.zebrunner.reporting.web.request.v1.HeadlessTestUpdateRequest;
import com.zebrunner.reporting.web.request.v1.TestFinishRequest;
import com.zebrunner.reporting.web.request.v1.TestRunFinishRequest;
import com.zebrunner.reporting.web.request.v1.TestRunStartRequest;
import com.zebrunner.reporting.web.request.v1.TestStartRequest;
import com.zebrunner.reporting.web.request.v1.TestUpdateRequest;
import com.zebrunner.reporting.web.response.v1.TestRunSaveResponse;
import com.zebrunner.reporting.web.response.v1.TestSaveResponse;
import com.zebrunner.reporting.web.util.JMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/test-runs", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestRunControllerV1 extends AbstractController {

    private final JMapper jMapper;
    private final Validator validator;
    private final TestRunServiceV1 testRunServiceV1;
    private final SimpMessagingTemplate messagingTemplate;
    private final LauncherCallbackService launcherCallbackService;
    private final TestRunStatisticsCacheableService statisticsCacheableService;

    @PostMapping
    public TestRunSaveResponse startTestRun(@RequestParam(name = "projectKey") String projectKey,
                                            @RequestBody @Validated TestRunStartRequest testRunStartRequest) {
        TestRun testRun = jMapper.map(testRunStartRequest, TestRun.class);
        testRun = testRunServiceV1.startRun(testRun, projectKey, getPrincipalId().longValue());

        com.zebrunner.reporting.domain.db.TestRun fullTestRun = testRunServiceV1.getTestRunFullById(testRun.getId());
        notifyAboutRunByWebsocket(fullTestRun);

        return jMapper.map(testRun, TestRunSaveResponse.class);
    }

    @PutMapping("/{testRunId}")
    public void finishTestRun(@PathVariable("testRunId") @NotNull @Positive Long testRunId,
                              @RequestBody @Validated TestRunFinishRequest testRunFinishRequest) {
        TestRun testRun = jMapper.map(testRunFinishRequest, TestRun.class);
        testRun.setId(testRunId);
        testRunServiceV1.finishRun(testRun);

        com.zebrunner.reporting.domain.db.TestRun testRunFull = testRunServiceV1.getTestRunFullById(testRunId);
        launcherCallbackService.notifyOnTestRunFinish(testRunFull.getCiRunId());

        notifyAboutRunByWebsocket(testRunFull);
    }

    @PostMapping("/{testRunId}/tests")
    public TestSaveResponse startTest(@PathVariable("testRunId") @NotNull @Positive Long testRunId,
                                      @RequestParam(name = "headless", defaultValue = "false") boolean headless,
                                      @RequestParam(name = "rerun", defaultValue = "false") boolean rerun,
                                      @RequestBody TestStartRequest testStartRequest) {
        Object request = headless
                ? jMapper.map(testStartRequest, HeadlessTestStartRequest.class)
                : testStartRequest;
        validate(request);

        Test test = jMapper.map(testStartRequest, Test.class);
        test = testRunServiceV1.startTest(test, testRunId, rerun);

        com.zebrunner.reporting.domain.db.Test oldTest = testRunServiceV1.getTestById(test.getId());
        notifyAboutTestByWebsocket(oldTest);

        return jMapper.map(test, TestSaveResponse.class);
    }

    @PutMapping("/{testRunId}/tests/{testId}")
    public TestSaveResponse updateTest(@PathVariable("testRunId") @NotNull @Positive Long testRunId,
                                       @PathVariable("testId") @NotNull @Positive Long testId,
                                       @RequestParam(name = "headless", defaultValue = "false") boolean headless,
                                       @RequestBody TestUpdateRequest testUpdateRequest) {
        Object request = headless
                ? jMapper.map(testUpdateRequest, HeadlessTestUpdateRequest.class)
                : jMapper.map(testUpdateRequest, TestFinishRequest.class);
        validate(request);

        Test test = jMapper.map(request, Test.class);
        test.setId(testId);

        if (headless) {
            testRunServiceV1.updateTest(test, testRunId);
        } else {
            testRunServiceV1.finishTest(test, testRunId);
        }

        com.zebrunner.reporting.domain.db.Test oldTest = testRunServiceV1.getTestById(testId);
        notifyAboutTestByWebsocket(oldTest);

        return jMapper.map(test, TestSaveResponse.class);
    }

    private void validate(Object object) {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @GetMapping("/{ciRunId}/tests")
    public List<TestSaveResponse> getTestsByCiRunId(@PathVariable("ciRunId") String ciRunId,
                                                    @RequestParam(name = "statuses", required = false) List<Status> statuses,
                                                    @RequestParam(name = "tests", required = false) List<Long> testIds) {
        return testRunServiceV1.getTestsByCiRunId(ciRunId, statuses, testIds).stream()
                               .map(test -> jMapper.map(test, TestSaveResponse.class))
                               .collect(Collectors.toList());
    }

    private void notifyAboutTestByWebsocket(com.zebrunner.reporting.domain.db.Test test) {
        notifyAboutTestRunStatisticsByWebsocket(test.getTestRunId());
        messagingTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
    }

    private void notifyAboutRunByWebsocket(com.zebrunner.reporting.domain.db.TestRun testRun) {
        // TODO: 3/20/20 hideJobUrlsIfNeed
        notifyAboutTestRunStatisticsByWebsocket(testRun.getId());
        messagingTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));
    }

    private void notifyAboutTestRunStatisticsByWebsocket(Long runId) {
        TestRunStatistics testRunStatistics = statisticsCacheableService.getTestRunStatistic(runId);
        messagingTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistics));
    }

}
