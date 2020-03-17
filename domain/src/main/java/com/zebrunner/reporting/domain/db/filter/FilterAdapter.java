package com.zebrunner.reporting.domain.db.filter;

import com.zebrunner.reporting.domain.dto.filter.Subject;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FilterAdapter {

    private String name;
    private String description;
    private Subject subject;
    private Long userId;
    private boolean publicAccess;

}
