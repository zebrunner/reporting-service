package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.persistence.utils.SQLTemplateAdapter;
import com.zebrunner.reporting.domain.db.Widget;
import com.zebrunner.reporting.persistence.utils.SQLAdapter;

import java.util.List;
import java.util.Map;

public interface WidgetMapper {
    List<Map<String, Object>> executeSQL(SQLAdapter sql);

    List<Map<String, Object>> executeSQLTemplate(SQLTemplateAdapter sql);

    void createWidget(Widget widget);

    Widget getWidgetById(Long id);

    List<Widget> getAllWidgets();

    void updateWidget(Widget widget);

    void deleteWidgetById(long id);
}
