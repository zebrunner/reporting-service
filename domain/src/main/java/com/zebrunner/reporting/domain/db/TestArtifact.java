package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TestArtifact extends AbstractEntity {
    private static final long serialVersionUID = 2708440751800176584L;

    private String name;
    private String link;
    private Date expiresAt;
    private Long testId;

    public boolean isValid() {
        return name != null && !name.isEmpty() && link != null && !link.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TestArtifact that = (TestArtifact) o;

        if (!Objects.equals(name, that.name))
            return false;
        if (!Objects.equals(link, that.link))
            return false;
        if (!Objects.equals(expiresAt, that.expiresAt))
            return false;
        return Objects.equals(testId, that.testId);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (expiresAt != null ? expiresAt.hashCode() : 0);
        result = 31 * result + (testId != null ? testId.hashCode() : 0);
        return result;
    }

}