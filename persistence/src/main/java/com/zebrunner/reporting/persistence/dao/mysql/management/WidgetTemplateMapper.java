package com.zebrunner.reporting.persistence.dao.mysql.management;

import com.zebrunner.reporting.domain.db.WidgetTemplate;

import java.util.List;

public interface WidgetTemplateMapper {

    WidgetTemplate getWidgetTemplateById(Long id);

    WidgetTemplate getWidgetTemplateByName(String name);

    List<WidgetTemplate> getAllWidgetTemplates();
}
