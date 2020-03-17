package com.zebrunner.reporting.persistence.dao.mysql.application.search;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestSearchCriteria extends SearchCriteria {

    private List<Long> testRunIds;
    private Long testRunId;
    private Long testCaseId;

}
