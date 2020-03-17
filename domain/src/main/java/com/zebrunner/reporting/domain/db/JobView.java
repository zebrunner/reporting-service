package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobView extends AbstractEntity {
    private static final long serialVersionUID = -3868077369004418496L;

    private Long viewId;
    private Job job = new Job();
    private String env;
    private Integer position;
    private Integer size;

}