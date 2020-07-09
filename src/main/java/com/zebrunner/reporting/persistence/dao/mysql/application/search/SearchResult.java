package com.zebrunner.reporting.persistence.dao.mysql.application.search;

import com.zebrunner.reporting.domain.db.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResult<T> extends SearchCriteria {

    private final List<T> results;
    private final Integer totalResults;

    @Builder
    public SearchResult(String query, String orderBy, Integer page, Integer pageSize, List<Project> projects, SortOrder sortOrder, List<T> results, Integer totalResults) {
        super(query, orderBy, page, pageSize, projects, sortOrder);
        this.results = results;
        this.totalResults = totalResults;
    }
}
