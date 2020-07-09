package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TestSuite extends AbstractEntity {
    private static final long serialVersionUID = -1847933012610222160L;

    private String name;
    private String fileName;
    private String description;
    private User user = new User();

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TestSuite && this.hashCode() == obj.hashCode());
    }

    @Override
    public int hashCode() {
        return (name + description + user.getId()).hashCode();
    }

}
