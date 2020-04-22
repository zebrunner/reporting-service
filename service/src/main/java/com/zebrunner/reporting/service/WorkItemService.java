package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.WorkItemMapper;
import com.zebrunner.reporting.domain.db.workitem.WorkItem;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkItemService.class);

    @Autowired
    private WorkItemMapper workItemMapper;

    @Transactional(rollbackFor = Exception.class)
    public void createWorkItem(WorkItem workItem) {
        validateWorkItemFieldsLength(workItem);
        workItemMapper.createWorkItem(workItem);
    }

    @Transactional(readOnly = true)
    public WorkItem getWorkItemById(long id) {
        return workItemMapper.getWorkItemById(id);
    }

    @Transactional(readOnly = true)
    public WorkItem getWorkItemByJiraIdAndType(String jiraId, WorkItem.Type type) {
        return workItemMapper.getWorkItemByJiraIdAndType(jiraId, type);
    }

    @Transactional(readOnly = true)
    public List<WorkItem> getWorkItemsByJiraIdAndType(String jiraId, WorkItem.Type type) {
        return workItemMapper.getWorkItemsByJiraIdAndType(jiraId, type);
    }

    @Transactional(readOnly = true)
    public WorkItem getWorkItemByJiraIdAndTypeAndHashcode(String jiraId, WorkItem.Type type, int hashcode) {
        return workItemMapper.getWorkItemByJiraIdAndTypeAndHashcode(jiraId, type, hashcode);
    }

    @Transactional(readOnly = true)
    public WorkItem getWorkItemByTestCaseIdAndHashCode(long testCaseId, int hashCode) {
        return workItemMapper.getWorkItemByTestCaseIdAndHashCode(testCaseId, hashCode);
    }

    @Transactional(readOnly = true)
    public List<WorkItem> getWorkItemsByTestCaseIdAndType(long testCaseId, WorkItem.Type type) {
        return workItemMapper.getWorkItemsByTestCaseIdAndType(testCaseId, type);
    }

    @Transactional(rollbackFor = Exception.class)
    public WorkItem updateWorkItem(WorkItem workItem) {
        validateWorkItemFieldsLength(workItem);
        workItemMapper.updateWorkItem(workItem);
        return workItem;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteKnownIssuesByTestId(long testId) {
        workItemMapper.deleteKnownIssuesByTestId(testId);
    }

    @Transactional(rollbackFor = Exception.class)
    public WorkItem createOrGetWorkItem(WorkItem newWorkItem) {
        WorkItem workItem = getWorkItemByJiraIdAndType(newWorkItem.getJiraId(), newWorkItem.getType());
        if (workItem == null) {
            createWorkItem(newWorkItem);
            return newWorkItem;
        } else {
            return workItem;
        }
    }

    private void validateWorkItemFieldsLength(WorkItem workItem) {
        String errorMessage = "";
        if (is45SymbolsLengthExceeded(workItem.getJiraId())) {
            errorMessage += "jiraId("+ workItem.getJiraId() +")";
        }
        if(StringUtils.isNotEmpty(errorMessage)){
            errorMessage = "WorkItem ID: "+ workItem.getId() + ", WorkItem JiraId: "+ workItem.getJiraId() + "\nFields exceeding 45 symbols restriction: " + errorMessage;
            LOGGER.error(errorMessage);
        }
    }

    private boolean is45SymbolsLengthExceeded(String value) {
        return StringUtils.isNotEmpty(value) && value.length() > 45;
    }
}
