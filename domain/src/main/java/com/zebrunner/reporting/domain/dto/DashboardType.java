package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.Attribute;
import com.zebrunner.reporting.domain.dto.widget.WidgetDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DashboardType extends AbstractType {

    private static final long serialVersionUID = -562795025453363474L;

    private String title;
    private List<WidgetDTO> widgets = new ArrayList<>();
    private boolean hidden;
    private Integer position;
    private boolean editable;
    private List<Attribute> attributes;

}