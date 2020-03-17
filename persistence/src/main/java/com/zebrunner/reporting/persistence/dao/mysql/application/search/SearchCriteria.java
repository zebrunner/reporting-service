package com.zebrunner.reporting.persistence.dao.mysql.application.search;

import com.zebrunner.reporting.domain.db.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String query;
    private String orderBy;

    // Pages are zero-based
    private Integer page = 1;
    // The very default page size, just not to get NPE'd
    private Integer pageSize = 20;
    private List<Project> projects;
    private SortOrder sortOrder = SortOrder.ASC;

    public enum SortOrder {
        ASC, DESC
    }

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }

    public void setProjects(List<Project> projects) {
        if (!CollectionUtils.isEmpty(projects)) {
            this.projects = projects;
        }
    }

}
