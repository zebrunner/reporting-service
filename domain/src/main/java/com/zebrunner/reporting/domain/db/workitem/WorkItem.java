package com.zebrunner.reporting.domain.db.workitem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.AbstractEntity;
import com.zebrunner.reporting.domain.db.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class WorkItem extends AbstractEntity {

    private static final long serialVersionUID = 5440580857483390564L;

    private String jiraId;
    private String description;
    private boolean blocker;
    private Integer hashCode;
    private Long testCaseId;
    private User user;
    // TODO: think about default type
    private Type type = Type.TASK;

    public WorkItem(String jiraId) {
        this.jiraId = jiraId;
    }

    public WorkItem(String jiraId, Type type) {
        this.jiraId = jiraId;
        this.type = type;
    }

    public enum Type {
        TASK,
        BUG,
        COMMENT
    }

}
