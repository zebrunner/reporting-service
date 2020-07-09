package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestSuiteType extends AbstractType {

    private static final long serialVersionUID = 6653114389767310676L;

    @NotNull
    private String name;
    @NotNull
    private String fileName;
    private String description;
    @NotNull
    private Long userId;

    public TestSuiteType(String name, String fileName, Long userId) {
        this.name = name;
        this.userId = userId;
        this.fileName = fileName;
    }

}
