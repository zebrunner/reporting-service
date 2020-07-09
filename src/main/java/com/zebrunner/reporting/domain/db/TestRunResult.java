package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRunResult {

    private Long testRunId;
    private Status status;
    private Long elapsed;

}
