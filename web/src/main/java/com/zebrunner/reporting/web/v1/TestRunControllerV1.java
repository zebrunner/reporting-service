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
import com.zebrunner.reporting.web.dto.TestDTO;
import com.zebrunner.reporting.web.dto.TestRunDTO;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
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

    private final Mapper mapper;
    private final Validator validator;
    private final TestRunServiceV1 testRunServiceV1;
    private final SimpMessagingTemplate messagingTemplate;
    private final LauncherCallbackService launcherCallbackService;
    private final TestRunStatisticsCacheableService statisticsCacheableService;

    @PostMapping
    public TestRunDTO startTestRun(
            @RequestBody @Validated(TestRunDTO.ValidationGroups.TestRunStartGroup.class) TestRunDTO testRunDTO,
            @RequestParam(name = "projectKey", required = false) String projectKey
    ) {
        TestRun testRun = mapper
                .map(testRunDTO, TestRun.class, TestRunDTO.ValidationGroups.TestRunStartGroup.class.getName());
        testRun = testRunServiceV1.startRun(testRun, projectKey, getPrincipalId());

        com.zebrunner.reporting.domain.db.TestRun fullTestRun = testRunServiceV1.getTestRunFullById(testRun.getId());
        notifyAboutRunByWebsocket(fullTestRun);

        testRunDTO = mapper
                .map(testRun, TestRunDTO.class, TestRunDTO.ValidationGroups.TestRunStartGroup.class.getName());
        return testRunDTO;
    }

    @PutMapping("/{id}")
    public void finishTestRun(
            @RequestBody @Validated(TestRunDTO.ValidationGroups.TestRunFinishGroup.class) TestRunDTO testRunDTO,
            @PathVariable("id") @NotNull @Positive Long id
    ) {
        TestRun testRun = mapper
                .map(testRunDTO, TestRun.class, TestRunDTO.ValidationGroups.TestRunFinishGroup.class.getName());
        testRun.setId(id);
        testRun = testRunServiceV1.finishRun(testRun);

        com.zebrunner.reporting.domain.db.TestRun testRunFull = testRunServiceV1.getTestRunFullById(id);
        launcherCallbackService.notifyOnTestRunFinish(testRunFull.getCiRunId());

        notifyAboutRunByWebsocket(testRunFull);
    }

    @PostMapping("/{id}/tests")
    public TestDTO startTest(
            @RequestBody TestDTO testDTO,
            @PathVariable("id") @NotNull @Positive Long id,
            @RequestParam(value = "headless", required = false) boolean headless,
            @RequestParam(value = "rerun", required = false) boolean rerun
    ) {
        validateOnTestStart(testDTO, headless);

        String mapperGroup = headless ? TestDTO.ValidationGroups.HeadlessTestStartGroup.class.getName()
                : TestDTO.ValidationGroups.TestStartGroup.class.getName();

        Test test = mapper.map(testDTO, Test.class, mapperGroup);
        test = testRunServiceV1.startTest(test, id, headless, rerun);

        com.zebrunner.reporting.domain.db.Test oldTest = testRunServiceV1.getTestById(test.getId());
        notifyAboutTestByWebsocket(oldTest);

        testDTO = mapper.map(test, TestDTO.class, TestDTO.class.getName());
        return testDTO;
    }

    private void validateOnTestStart(TestDTO testDTO, boolean headless) {
        Class<?> validationGroup = headless ?
                TestDTO.ValidationGroups.HeadlessTestStartGroup.class :
                TestDTO.ValidationGroups.TestStartGroup.class;

        Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO, validationGroup);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @PutMapping("/{id}/tests/{testId}")
    public void finishTest(
            @RequestBody @Validated(TestDTO.ValidationGroups.TestFinishGroup.class) TestDTO testDTO,
            @PathVariable("id") @NotNull @Positive Long id,
            @PathVariable("testId") @NotNull @Positive Long testId
    ) {
        Test test = mapper.map(testDTO, Test.class, TestDTO.ValidationGroups.TestFinishGroup.class.getName());
        test.setId(testId);

        test = testRunServiceV1.finishTest(test, id);

        com.zebrunner.reporting.domain.db.Test oldTest = testRunServiceV1.getTestById(testId);
        notifyAboutTestByWebsocket(oldTest);
    }

    @GetMapping("/{ciRunId}/tests")
    public List<TestDTO> getTestsByCiRunId(
            @PathVariable("ciRunId") String ciRunId,
            @RequestParam(name = "statuses", required = false) List<Status> statuses,
            @RequestParam(name = "tests", required = false) List<Long> testIds
    ) {
        return testRunServiceV1.getTestsByCiRunId(ciRunId, statuses, testIds).stream()
                               .map(test -> mapper
                                       .map(test, TestDTO.class, TestDTO.ValidationGroups.TestStartGroup.class
                                               .getName()))
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
