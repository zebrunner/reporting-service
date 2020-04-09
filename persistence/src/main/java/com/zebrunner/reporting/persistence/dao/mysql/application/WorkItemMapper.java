package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.List;

import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import org.apache.ibatis.annotations.Param;

public interface WorkItemMapper {
    void createWorkItem(WorkItem workItem);

    WorkItem getWorkItemById(long id);

    WorkItem getWorkItemByJiraIdAndType(@Param("jiraId") String jiraId, @Param("type") WorkItem.Type type);

    List<WorkItem> getWorkItemsByJiraIdAndType(@Param("jiraId") String jiraId, @Param("type") WorkItem.Type type);

    WorkItem getWorkItemByJiraIdAndTypeAndHashcode(@Param("jiraId") String jiraId, @Param("type") WorkItem.Type type, @Param("hashCode") int hashCode);

    WorkItem getWorkItemByTestCaseIdAndHashCode(@Param("testCaseId") long testCaseId, @Param("hashCode") int hashCode);

    List<WorkItem> getWorkItemsByTestCaseIdAndType(@Param("testCaseId") long testCaseId, @Param("type") WorkItem.Type type);

    void updateWorkItem(WorkItem workItem);

    void deleteWorkItemById(long id);

    void deleteWorkItem(WorkItem workItem);

    void deleteKnownIssuesByTestId(long id);
}
