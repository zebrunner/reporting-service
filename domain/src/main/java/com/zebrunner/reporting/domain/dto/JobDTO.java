package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class JobDTO extends AbstractType {

    private static final long serialVersionUID = 4123576956700125643L;

    @NotNull
    private String name;
    @NotNull
    private String jobURL;
    @NotNull
    private String jenkinsHost;
    @NotNull
    private Long userId;

    private Long automationServerId;

}
