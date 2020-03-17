package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TestArtifactType extends AbstractType {
    private static final long serialVersionUID = 555233394837989532L;

    private String name;
    private String link;
    private Long testId;
    private Integer expiresIn;

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof TestArtifactType) {
            equals = this.name.equals(((TestArtifactType) obj).getName());
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link);
    }
}