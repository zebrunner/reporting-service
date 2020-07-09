package com.zebrunner.reporting.domain.dto.widget;

import com.zebrunner.reporting.domain.db.WidgetTemplate;
import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetTemplateDTO extends AbstractType {

    private static final long serialVersionUID = 7998270816228259812L;

    private String name;
    private String description;
    private WidgetTemplate.Type type;
    private String chartConfig;
    private String paramsConfig;
    private String legendConfig;
    private Boolean hidden;

}
