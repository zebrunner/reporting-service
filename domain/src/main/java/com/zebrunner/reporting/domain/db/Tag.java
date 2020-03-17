package com.zebrunner.reporting.domain.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends AbstractEntity {

    private static final long serialVersionUID = 4886769517837569318L;

    private String name;
    private String value;

    @Override
    public int hashCode() {
        return (this.name + this.value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Tag && this.hashCode() == obj.hashCode();
    }
}
