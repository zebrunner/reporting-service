package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Widget extends AbstractEntity {

    private static final long serialVersionUID = -750759195176951157L;

    private String title;
    private String description;
    private String paramsConfig;
    private String legendConfig;
    private WidgetTemplate widgetTemplate;
    private boolean refreshable;
    private String type;
    private Integer size;
    private Integer position;
    private String location;
    private String sql;
    private String model;

}
