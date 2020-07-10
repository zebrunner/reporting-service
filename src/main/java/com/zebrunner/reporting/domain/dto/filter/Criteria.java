package com.zebrunner.reporting.domain.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Criteria {

    @NotNull(message = "Criteria name required")
    private Name name;

    @NotNull(message = "Operator required")
    private Operator operator;

    private List<Operator> operators;
    private String value;

    @JsonIgnore
    @AssertTrue(message = "Incorrect value")
    public boolean isValueNull() {
        return Arrays.asList(Operator.LAST_24_HOURS, Operator.LAST_7_DAYS, Operator.LAST_14_DAYS, Operator.LAST_30_DAYS).contains(this.operator) == (value == null);
    }

    public enum Name {
        STATUS,
        TEST_SUITE,
        JOB_URL,
        ENV,
        PLATFORM,
        BROWSER,
        LOCALE,
        DATE,
        PROJECT
    }
}
