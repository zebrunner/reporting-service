package com.zebrunner.reporting.persistence.dao.mysql.application.search;

import com.zebrunner.reporting.domain.db.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserSearchCriteria extends SearchCriteria implements DateSearchCriteria {

    private User.Status status;
    private Date date;
    private Date fromDate;
    private Date toDate;

}
