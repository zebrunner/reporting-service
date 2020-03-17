package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueueTestRunParamsType implements Serializable {

    private static final long serialVersionUID = 5893913105698710480L;

    private String jobUrl;
    private String branch;
    private String env;
    private String ciRunId;
    private String ciParentUrl;
    private String ciParentBuild;
    private String buildNumber;
    private String project;

}
