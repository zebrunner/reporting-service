package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPreference extends AbstractEntity {

    private static final long serialVersionUID = 3544699350398796894L;

    private Name name;
    private String value;
    private Long userId;

    public enum Name {
        DEFAULT_DASHBOARD,
        DEFAULT_TEST_VIEW,
        REFRESH_INTERVAL,
        THEME
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreference that = (UserPreference) o;
        return name == that.name &&
                Objects.equals(value, that.value) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, userId);
    }
}
