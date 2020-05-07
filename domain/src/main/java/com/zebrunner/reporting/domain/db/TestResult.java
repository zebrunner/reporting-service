package com.zebrunner.reporting.domain.db;

import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TestResult {

    private Long testId;
    private Status status;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private Long elapsed;
    private List<WorkItem> workItems;

}
