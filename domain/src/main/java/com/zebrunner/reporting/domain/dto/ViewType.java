package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ViewType extends AbstractEntity {

    private static final long serialVersionUID = 8779340419016013263L;

    @NotNull
    private String name;
    @NotNull
    private Long projectId;

}