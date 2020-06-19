package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.TestResult;
import com.zebrunner.reporting.domain.db.workitem.WorkItemBatch;
import com.zebrunner.reporting.domain.dto.TestResultDTO;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestSearchCriteria;
import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.TestArtifact;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import com.zebrunner.reporting.domain.dto.IssueDTO;
import com.zebrunner.reporting.domain.dto.TestArtifactDTO;
import com.zebrunner.reporting.domain.dto.TestRunStatistics;
import com.zebrunner.reporting.domain.dto.TestType;
import com.zebrunner.reporting.domain.push.TestPush;
import com.zebrunner.reporting.domain.push.TestRunPush;
import com.zebrunner.reporting.domain.push.TestRunStatisticPush;
import com.zebrunner.reporting.service.TestArtifactService;
import com.zebrunner.reporting.service.TestRunService;
import com.zebrunner.reporting.service.TestService;
import com.zebrunner.reporting.service.WorkItemService;
import com.zebrunner.reporting.service.cache.TestRunStatisticsCacheableService;
import com.zebrunner.reporting.service.integration.tool.impl.TestCaseManagementService;
import com.zebrunner.reporting.web.documented.TestDocumentedController;
import com.zebrunner.reporting.web.util.patch.BatchPatchDescriptor;
import com.zebrunner.reporting.web.util.patch.PatchDecorator;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequestMapping(path = "api/tests", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestController extends AbstractController implements TestDocumentedController {

    @Autowired
    private Mapper mapper;

    @Autowired
    private TestService testService;

    @Autowired
    private TestArtifactService testArtifactService;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private WorkItemService workItemService;

    @Autowired
    private TestCaseManagementService testCaseManagementService;

    @Autowired
    private SimpMessagingTemplate websocketTemplate;

    @Autowired
    private TestRunStatisticsCacheableService statisticsService;

    @PostMapping()
    @Override
    public TestType startTest(@Valid @RequestBody TestType t) {
        Test test = testService.startTest(mapper.map(t, Test.class), t.getWorkItems(), t.getConfigXML());
        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
        return mapper.map(test, TestType.class);
    }

    @PostMapping("/{id}/finish")
    @Override
    public TestType finishTest(@PathVariable("id") long id, @RequestBody TestType t) {
        Test test = mapper.map(t, Test.class);
        test.setId(id);
        test = testService.finishTest(test, t.getConfigXML(), t.getTestMetrics());

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
        return mapper.map(test, TestType.class);
    }

    @PreAuthorize("hasPermission('MODIFY_TESTS')")
    @PutMapping()
    @Override
    public Test updateTest(@RequestBody Test test) {
        Test updatedTest = testService.changeTestStatus(test.getId(), test.getStatus());

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(updatedTest.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(updatedTest.getTestRunId()), new TestPush(updatedTest));

        TestRun testRun = testRunService.getTestRunById(updatedTest.getTestRunId());
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));

        return updatedTest;
    }

    @PreAuthorize("hasPermission('MODIFY_TESTS')")
    @PatchMapping("/runs/{testRunId}")
    @Override
    public List<Test> batchPatch(
            @RequestBody @Valid BatchPatchDescriptor batchPatchDescriptor,
            @PathVariable("testRunId") Long testRunId
    ) {
        List<Test> tests = PatchDecorator.<List<Test>, Status>descriptor(batchPatchDescriptor)
                .operation(PatchOperation.class)

                .when(PatchOperation.STATUS_UPDATE)
                .withParameter(Status::valueOf)
                .then(status -> testService.batchStatusUpdate(testRunId, batchPatchDescriptor.getIds(), status))

                .after()
                .decorate();

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(testRunId);
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));

        tests.forEach(test -> websocketTemplate.convertAndSend(getTestsWebsocketPath(testRunId), new TestPush(test)));

        TestRun testRun = testRunService.getTestRunById(testRunId);
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));

        return tests;
    }

    enum PatchOperation {
        STATUS_UPDATE
    }

    @PostMapping("/{id}/workitems")
    @Override
    public TestType createTestWorkItems(
            @PathVariable("id") long id,
            @RequestBody List<String> workItems
    ) {
        Test test = testService.createTestWorkItems(id, workItems);
        return mapper.map(test, TestType.class);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteTest(@PathVariable("id") long id) {
        testService.deleteTestById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/search")
    @Override
    public SearchResult<Test> searchTests(@RequestBody TestSearchCriteria sc) {
        return testService.searchTests(sc);
    }

    @GetMapping("/{id}/history")
    @Override
    public List<TestResultDTO> getTestResultsById(@PathVariable("id") Long id,
                                                  @RequestParam("limit") Long limit) {
        List<TestResult> results = testService.getLatestTestResultsByTestId(id, limit);
        return results.stream()
                      .map(stability -> mapper.map(stability, TestResultDTO.class))
                      .collect(Collectors.toList());
    }

    @GetMapping("/{id}/workitem/{type}")
    @Override
    public List<WorkItem> getTestCaseWorkItemsByType(
            @PathVariable("id") long id,
            @PathVariable("type") WorkItem.Type type
    ) {
        return testService.getTestCaseWorkItems(id, type);
    }

    @PostMapping("/{id}/workitem")
    @Override
    public WorkItem linkWorkItem(@PathVariable("id") long id, @RequestBody WorkItem workItem) {
        if (getPrincipalId() > 0) {
            workItem.setUser(new User(getPrincipalId()));
        }

        workItem = testService.linkWorkItem(id, workItem);

        Test test = testService.getTestById(id);
        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

        TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));

        return workItem;
    }

    @PostMapping("/runs/{testRunId}/workitems")
    @Override
    public List<WorkItemBatch> linkWorkItems(@RequestBody List<WorkItemBatch> workItemBatches, @PathVariable("testRunId") Long testRunId) {

        workItemBatches = workItemBatches.stream()
                                         .filter(Objects::nonNull)
                                         .collect(Collectors.toList());

        if (getPrincipalId() > 0) {
            setUserToWorkItems(workItemBatches);
        }

        workItemBatches = testService.linkWorkItems(workItemBatches, testRunId);

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(testRunId);
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));

        workItemBatches.forEach(workItemBatch -> {
            Test test = testService.getTestById(workItemBatch.getTestId());
            websocketTemplate.convertAndSend(getTestsWebsocketPath(testRunId), new TestPush(test));
        });

        TestRun testRun = testRunService.getTestRunById(testRunId);
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));

        return workItemBatches;
    }

    private void setUserToWorkItems(List<WorkItemBatch> workItemBatches) {
        workItemBatches.forEach(workItemBatch ->
                workItemBatch.getWorkItems().forEach(workItem -> workItem.setUser(new User(getPrincipalId()))));
    }

    @PutMapping("/{id}/issues")
    @Override
    public WorkItem updateTestKnownIssue(
            @PathVariable("id") long id,
            @RequestBody WorkItem workItem
    ) {
        return testService.updateTestWorkItem(id, workItem);
    }

    @DeleteMapping("/{testId}/workitem/{workItemId}")
    @Override
    public void deleteTestWorkItem(@PathVariable("workItemId") long workItemId, @PathVariable("testId") long testId) {
        Test test = testService.getTestById(testId);
        WorkItem workItem = workItemService.getWorkItemById(workItemId);
        testService.deleteTestWorkItem(testId, workItemId);

        if (WorkItem.Type.BUG.equals(workItem.getType())) {
            TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
            websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
            websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

            TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
            websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));
        }
    }

    // // TODO: 11/1/19 get rid of jira endpoints
    @GetMapping("/jira/{issue}")
    @Override
    public IssueDTO getJiraIssue(@PathVariable("issue") String issue) {
        return testCaseManagementService.getIssue(issue);
    }

    @GetMapping("/jira/connect")
    @Override
    public boolean getConnectionToJira() {
        return testCaseManagementService.isEnabledAndConnected(null);
    }

    @PostMapping("/{id}/artifacts")
    @Override
    public void addTestArtifact(
            @PathVariable("id") long id,
            @RequestBody TestArtifactDTO artifact
    ) {
        artifact.setTestId(id);
        testArtifactService.createOrUpdateTestArtifact(mapper.map(artifact, TestArtifact.class));
        // Updating web client with latest artifacts
        Test test = testService.getTestById(id);
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
    }

    @PostMapping("/{id}/artifacts/batch")
    @Override
    public void addTestArtifacts(
            @PathVariable("id") long id,
            @RequestBody List<TestArtifactDTO> testArtifactDTOs
    ) {
        List<TestArtifact> artifacts = testArtifactDTOs.stream()
                                                       .map(testArtifactDTO -> mapper.map(testArtifactDTO, TestArtifact.class))
                                                       .collect(Collectors.toList());
        testArtifactService.attachTestArtifacts(id, artifacts);
        // Updating web client with latest artifacts
        Test test = testService.getTestById(id);
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));
    }

}
