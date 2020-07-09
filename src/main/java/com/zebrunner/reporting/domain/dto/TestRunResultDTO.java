package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRunResultDTO {

    private Long testRunId;
    private Status status;
    private Long elapsed;

}
