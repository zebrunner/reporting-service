package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.List;

import com.zebrunner.reporting.domain.db.Project;

public interface ProjectMapper {
    void createProject(Project project);

    Project getProjectById(long id);

    Project getProjectByName(String name);

    List<Project> getAllProjects();

    void updateProject(Project project);

    void deleteProjectById(long id);
}
