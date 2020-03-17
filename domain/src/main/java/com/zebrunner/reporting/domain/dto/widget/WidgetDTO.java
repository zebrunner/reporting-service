package com.zebrunner.reporting.domain.dto.widget;

import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

@Getter
@Setter
public class WidgetDTO extends AbstractType {

    private static final long serialVersionUID = -8163778207543974125L;

    @NotEmpty(message = "{error.title.required}")
    private String title;

    private String description;
    private String paramsConfig;
    private String legendConfig;

    @Valid
    private WidgetTemplateDTO widgetTemplate;

    private boolean refreshable;
    private String type;
    private Integer size;
    private Integer position;
    private String location;
    private String sql;
    private String model;

}
