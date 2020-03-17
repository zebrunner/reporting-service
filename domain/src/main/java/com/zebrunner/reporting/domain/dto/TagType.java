package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@Getter
@Setter
public class TagType extends AbstractType {

    private static final long serialVersionUID = 5534633175290893478L;

    @NotEmpty(message = "Name required")
    @Size(max = 50)
    private String name;

    @NotEmpty(message = "Value required")
    @Size(max = 255)
    private String value;

    @Override
    public int hashCode() {
        return (this.name + this.value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TagType && this.hashCode() == obj.hashCode();
    }

}
