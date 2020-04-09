package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Dashboard extends AbstractEntity {

    private static final long serialVersionUID = -562795025453363474L;

    private String title;
    private List<Widget> widgets = new ArrayList<>();
    private boolean hidden;
    private Integer position;
    private boolean editable;
    private boolean system;
    private List<Attribute> attributes;

}