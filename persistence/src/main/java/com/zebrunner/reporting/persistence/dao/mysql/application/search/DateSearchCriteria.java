package com.zebrunner.reporting.persistence.dao.mysql.application.search;

import java.util.Date;

/**
 * Created by irina on 4.7.17.
 */
public interface DateSearchCriteria {

    Date getDate();

    void setDate(Date Date);

    Date getFromDate();

    void setFromDate(Date fromDate);

    Date getToDate();

    void setToDate(Date toDate);

}
