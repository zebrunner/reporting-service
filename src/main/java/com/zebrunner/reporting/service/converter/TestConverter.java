package com.zebrunner.reporting.service.converter;

import com.zebrunner.reporting.domain.db.Tag;
import com.zebrunner.reporting.domain.db.TestCase;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.reporting.Test;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TestConverter {

    public com.zebrunner.reporting.domain.db.Test toLegacyModel(Test test, Long testRunId) {
        com.zebrunner.reporting.domain.db.Test legacyTest = new com.zebrunner.reporting.domain.db.Test();
        legacyTest.setId(test.getId());
        legacyTest.setName(test.getName());
        legacyTest.setTestRunId(testRunId);
        legacyTest.setOwner(test.getMaintainer());
        legacyTest.setUuid(test.getUuid());

        if (test.getTags() != null) {
            Set<Tag> tags = test.getTags().stream()
                                .map(tag -> new Tag(tag, null))
                                .collect(Collectors.toSet());
            legacyTest.setTags(tags);
        }

        // TODO: 3/20/20 additional attributes

        return legacyTest;
    }

    public Test fromLegacyModel(com.zebrunner.reporting.domain.db.Test legacyTest, TestCase testCase) {
        Test test = new Test();

        test.setId(legacyTest.getId());
        test.setUuid(legacyTest.getUuid());
        test.setName(legacyTest.getName());
        test.setMaintainer(legacyTest.getOwner());
        test.setResult(legacyTest.getStatus().name());

        test.setClassName(testCase.getTestClass());
        test.setMethodName(testCase.getTestMethod());
        test.setTestCase(testCase.getInfo());

        return test;
    }

    public TestCase toTestCase(Test test, com.zebrunner.reporting.domain.db.TestRun testRun, User caseOwner) {
        TestCase testCase = new TestCase();

        testCase.setTestClass(test.getClassName());
        testCase.setTestMethod(test.getMethodName());
        testCase.setInfo(test.getTestCase()); // for now in info
        testCase.setPrimaryOwner(caseOwner);
        testCase.setProject(testRun.getProject());
        testCase.setTestSuiteId(testRun.getTestSuite().getId());

        return testCase;
    }

}
