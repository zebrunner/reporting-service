package com.zebrunner.reporting.persistence.dao.mysql.application.search;

import com.zebrunner.reporting.domain.db.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class TestRunSearchCriteria extends SearchCriteria implements DateSearchCriteria {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date toDate;

    private Long id;
    private Long testSuiteId;
    private String environment;
    private String platform;
    private String browser;
    private Status status;
    private Boolean reviewed;
    private FilterSearchCriteria filterSearchCriteria;
    private String locale;

    public TestRunSearchCriteria() {
        super.setSortOrder(SortOrder.DESC);
    }

    public void setFromDateString(String fromDate) throws ParseException {
        this.fromDate = new SimpleDateFormat("MM-dd-yyyy").parse(fromDate);
    }

    public void setToDateString(String toDate) throws ParseException {
        this.toDate = new SimpleDateFormat("MM-dd-yyyy").parse(toDate);
    }

}