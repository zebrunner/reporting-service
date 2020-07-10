package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TestResultDTO {

    private Long testId;
    private Long testRunId;
    private Status status;
    private LocalDateTime startTime;
    private Long elapsed;
    private List<WorkItem> workItems;

}
