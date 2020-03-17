package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.View;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ViewMapper {
    void createView(View view);

    View getViewById(long id);

    List<View> getAllViews(@Param("projectId") Long projectId);

    void updateView(View view);

    void reassignToProject(@Param("fromProjectId") Long fromProjectId, @Param("toProjectId") Long toProjectId);

    void deleteViewById(long id);
}