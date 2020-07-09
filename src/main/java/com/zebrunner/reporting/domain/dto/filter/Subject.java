package com.zebrunner.reporting.domain.dto.filter;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class Subject {

    @NotNull(message = "Subject name required")
    private Name name;
    @Valid
    private List<Criteria> criterias;

    public enum Name {
        TEST_RUN
    }

    public void sortCriterias() {
        this.criterias.sort(Comparator.comparing(Criteria::getName));
    }

}
