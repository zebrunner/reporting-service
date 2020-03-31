package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.TestCaseMapper;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.TestCaseSearchCriteria;
import com.zebrunner.reporting.domain.db.Project;
import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.TestCase;
import com.zebrunner.reporting.service.cache.TestCaseCacheableService;
import com.zebrunner.reporting.service.project.ProjectReassignable;
import com.zebrunner.reporting.service.project.ProjectService;
import com.zebrunner.reporting.service.util.DateTimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class TestCaseService implements ProjectReassignable {

    private final TestCaseMapper testCaseMapper;
    private final ProjectService projectService;
    private final TestCaseCacheableService testCaseCacheableService;

    public TestCaseService(TestCaseMapper testCaseMapper, ProjectService projectService, TestCaseCacheableService testCaseCacheableService) {
        this.testCaseMapper = testCaseMapper;
        this.projectService = projectService;
        this.testCaseCacheableService = testCaseCacheableService;
    }

    @Transactional
    public void createTestCase(TestCase testCase) {
        if (testCase.getStatus() == null) {
            testCase.setStatus(Status.UNKNOWN);
        }
        testCaseMapper.createTestCase(testCase);
    }

    @Transactional(readOnly = true)
    public TestCase getTestCaseById(long id) {
        return testCaseMapper.getTestCaseById(id);
    }

    @Transactional(readOnly = true)
    public TestCase getOwnedTestCase(Long userId, String testClass, String testMethod, Long projectId) {
        return testCaseCacheableService.getOwnedTestCase(userId, testClass, testMethod, projectId);
    }

    @Transactional
    public TestCase updateTestCase(TestCase testCase) {
        testCaseMapper.updateTestCase(testCase);
        return testCase;
    }

    @Transactional
    public void batchStatusUpdate(List<Long> ids, Status status) {
        testCaseMapper.updateStatuses(ids, status);
    }

    @Transactional
    public TestCase createOrUpdateCase(TestCase testCase, String projectName) {
        Project project = projectService.getProjectByNameOrDefault(projectName);
        testCase.setProject(project);
        return createOrUpdateCase(testCase);
    }

    @Transactional
    public TestCase createOrUpdateCase(TestCase newTestCase) {
        Project project = newTestCase.getProject();
        TestCase testCase = getOwnedTestCase(newTestCase.getPrimaryOwner().getId(), newTestCase.getTestClass(), newTestCase.getTestMethod(), project.getId());
        if (testCase == null || !testCase.getProject().getName().equals(project.getName())) {
            createTestCase(newTestCase);
        } else if (!testCase.equals(newTestCase)) {
            newTestCase.setId(testCase.getId());
            updateTestCase(newTestCase);
        } else {
            newTestCase = testCase;
        }
        return newTestCase;
    }

    @Transactional
    public TestCase[] createOrUpdateCases(TestCase[] testCases, String projectName) {
        Project project = projectService.getProjectByName(projectName);
        Arrays.stream(testCases)
              .forEach(testCase -> testCase.setProject(project));
        return createOrUpdateCases(testCases);
    }

    @Transactional
    public TestCase[] createOrUpdateCases(TestCase[] newTestCases) {
        int index = 0;
        for (TestCase newTestCase : newTestCases) {
            newTestCases[index++] = createOrUpdateCase(newTestCase);
        }
        return newTestCases;
    }

    @Transactional(readOnly = true)
    public SearchResult<TestCase> searchTestCases(TestCaseSearchCriteria sc) {
        DateTimeUtil.actualizeSearchCriteriaDate(sc);

        List<TestCase> testCases = testCaseMapper.searchTestCases(sc);
        int count = testCaseMapper.getTestCasesSearchCount(sc);

        return SearchResult.<TestCase>builder()
                .page(sc.getPage())
                .pageSize(sc.getPageSize())
                .sortOrder(sc.getSortOrder())
                .results(testCases)
                .totalResults(count)
                .build();
    }

    @Override
    @Transactional
    public void reassignProject(Long fromId, Long toId) {
        testCaseMapper.reassignToProject(fromId, toId);
    }
}
