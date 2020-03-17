package com.zebrunner.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IssueDTO {

    private final String assigneeName;
    private final String reporterName;
    private final String summary;
    private final String status;

}
