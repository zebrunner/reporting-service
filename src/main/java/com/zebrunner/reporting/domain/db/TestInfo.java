package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestInfo {

    private String tagValue;
    private String id;
    private String status;
    private String message;
    private String defectId;

}
