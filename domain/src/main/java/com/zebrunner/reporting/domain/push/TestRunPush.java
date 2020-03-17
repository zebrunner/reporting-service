package com.zebrunner.reporting.domain.push;

import com.zebrunner.reporting.domain.db.TestRun;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRunPush extends AbstractPush {

    private TestRun testRun;

    public TestRunPush(TestRun testRun) {
        super(Type.TEST_RUN);
        this.testRun = testRun;
    }

}
