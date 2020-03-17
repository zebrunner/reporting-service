package com.zebrunner.reporting.domain.push;

import com.zebrunner.reporting.domain.dto.TestRunStatistics;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRunStatisticPush extends AbstractPush {
    private TestRunStatistics testRunStatistics;

    public TestRunStatisticPush(TestRunStatistics testRunStatistics) {
        super(Type.TEST_RUN_STATISTICS);
        this.testRunStatistics = testRunStatistics;
    }

}
