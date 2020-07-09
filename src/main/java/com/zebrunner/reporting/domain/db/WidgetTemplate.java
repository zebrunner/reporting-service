package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetTemplate extends AbstractEntity {

    private static final long serialVersionUID = -704907868278795109L;

    private String name;
    private String description;
    private Type type;
    private String sql;
    private String chartConfig;
    private String paramsConfig;
    private String legendConfig;
    private Boolean hidden;

    public enum Type {
        PIE,
        LINE,
        BAR,
        TABLE,
        OTHER
    }

}
