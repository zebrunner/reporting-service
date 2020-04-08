package com.zebrunner.reporting.domain.db.workitem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WorkItemBatch {

    private Long testId;
    private List<WorkItem> workItems;

}
