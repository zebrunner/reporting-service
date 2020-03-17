package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Project extends AbstractEntity {
    private static final long serialVersionUID = 1489890001065170767L;

    private String name;
    private String description;

    public Project(String name) {
        this.name = name;
    }

}
