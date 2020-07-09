package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestRunStatistics implements Serializable {

    private static final long serialVersionUID = -1915862891525912222L;

    private long testRunId;
    private int passed;
    private int failed;
    private int failedAsKnown;
    private int failedAsBlocker;
    private int skipped;
    private int inProgress;
    private int aborted;
    private int queued;
    private boolean reviewed;

    public enum Action {
        MARK_AS_KNOWN_ISSUE,
        REMOVE_KNOWN_ISSUE,
        MARK_AS_BLOCKER,
        REMOVE_BLOCKER,
        MARK_AS_REVIEWED,
        MARK_AS_NOT_REVIEWED
    }

}
