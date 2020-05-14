package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.Status;
import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestResultDTO {

    private Long testId;
    private Status status;
    private Long elapsed;
    private List<WorkItem> workItems;

}
