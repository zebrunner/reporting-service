package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JenkinsJobsScanResultDTO implements Serializable {

    private static final long serialVersionUID = -5754742673797369219L;

    @NotEmpty
    private String repo;

    @NotNull
    @Min(1)
    private Long userId;
    private List<JenkinsJobDTO> jenkinsJobs;
    private boolean success;

}