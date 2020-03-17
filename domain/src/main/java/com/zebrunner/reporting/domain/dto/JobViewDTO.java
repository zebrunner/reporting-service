package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.AbstractEntity;
import com.zebrunner.reporting.domain.db.Job;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class JobViewDTO extends AbstractEntity {

    private static final long serialVersionUID = -3868077369004418496L;

    @NotNull
    private Job job;
    @NotNull
    private Long viewId;
    @NotNull
    private String env;
    @NotNull
    private Integer position;
    @NotNull
    private Integer size;

}