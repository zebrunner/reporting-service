package com.zebrunner.reporting.persistence.repository;

import com.zebrunner.reporting.domain.entity.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Optional<Project> findByName(String name);
}
