package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JenkinsJobDTO implements Serializable {

    private static final long serialVersionUID = -5754742673797369219L;

    @NotEmpty(message = "{error.job.url.required}")
    private String url;

    @NotNull(message = "{error.job.parameters.required}")
    private String parameters;

    private String type;
}
