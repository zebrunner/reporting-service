package com.zebrunner.reporting.domain.dto.widget;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetTemplateParameter {

    private List<Object> values;
    private String valuesQuery;
    private Object value;
    private String type;
    private Boolean multiple;
    private Boolean hidden;
    private Boolean required;

}
