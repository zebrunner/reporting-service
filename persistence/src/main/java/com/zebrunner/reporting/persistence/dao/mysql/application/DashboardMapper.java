package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.List;

import com.zebrunner.reporting.domain.db.Attribute;
import com.zebrunner.reporting.domain.db.Dashboard;
import com.zebrunner.reporting.domain.db.Widget;
import org.apache.ibatis.annotations.Param;

public interface DashboardMapper {
    void createDashboard(Dashboard dashboard);

    Dashboard getDashboardById(Long id);

    Dashboard getDashboardByTitle(String title);

    Dashboard getDefaultDashboardByUserId(Long userId);

    List<Dashboard> getAllDashboards();

    void updateDashboard(Dashboard dashboard);

    void updateDashboardOrder(@Param("id") long id, @Param("position") int position);

    void deleteDashboardById(long id);

    void addDashboardWidget(@Param("dashboardId") Long dashboardId, @Param("widget") Widget widget);

    void deleteDashboardWidget(@Param("dashboardId") Long dashboardId, @Param("widgetId") Long widgetId);

    void updateDashboardWidget(@Param("dashboardId") Long dashboardId, @Param("widget") Widget widget);

    List<Dashboard> getDashboardsByHidden(boolean hidden);

    List<Attribute> getAttributesByDashboardId(long dashboardId);

    Attribute getAttributeById(long attributeId);

    void createDashboardAttribute(@Param("dashboardId") long dashboardId, @Param("attribute") Attribute attribute);

    void updateAttribute(Attribute attribute);

    void deleteDashboardAttributeById(long attributeId);
}
