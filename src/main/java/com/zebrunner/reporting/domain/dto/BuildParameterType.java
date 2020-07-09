package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildParameterType extends AbstractType {
    private static final long serialVersionUID = 3647801256222745555L;

    @NotNull
    private BuildParameterClass parameterClass;
    @NotNull
    private String name;
    @NotNull
    private String value;

    public enum BuildParameterClass {
        STRING,
        BOOLEAN,
        HIDDEN
    }

}
