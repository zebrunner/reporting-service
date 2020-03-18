package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.Widget;
import com.zebrunner.reporting.persistence.utils.SQLAdapter;
import com.zebrunner.reporting.persistence.utils.SQLTemplateAdapter;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WidgetMapper {

    List<Map<String, Object>> executeSQL(SQLAdapter sql);

    List<Map<String, Object>> executeSQLTemplate(SQLTemplateAdapter sql);

    void createWidget(Widget widget);

    Widget getWidgetById(Long id);

    Widget getWidgetByTitleAndTemplateId(@Param("title") String title, @Param("templateId") Long templateId);

    List<Widget> getAllWidgets();

    void updateWidget(Widget widget);

    void deleteWidgetById(long id);
}
