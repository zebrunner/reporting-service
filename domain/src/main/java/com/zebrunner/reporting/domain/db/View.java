package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class View extends AbstractEntity {
    private static final long serialVersionUID = 3795611752266419360L;

    private String name;
    private Project project = new Project();

}