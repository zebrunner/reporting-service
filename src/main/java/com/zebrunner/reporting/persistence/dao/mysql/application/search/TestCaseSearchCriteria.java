package com.zebrunner.reporting.persistence.dao.mysql.application.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TestCaseSearchCriteria extends SearchCriteria implements DateSearchCriteria {

    private Long id;
    private List<Long> ids;
    private String testClass;
    private String testMethod;
    private String testSuiteName;
    private String testSuiteFile;
    private String username;
    private Date date;
    private Date fromDate;
    private Date toDate;
    private String period;

    public TestCaseSearchCriteria(List<Long> ids) {
        this.ids = ids;
    }

    public void addId(Long id) {
        if (this.ids == null) {
            this.ids = new ArrayList<>();
        }
        this.ids.add(id);
    }

}
