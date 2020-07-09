package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zebrunner.reporting.domain.db.Tenancy;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Arrays;

@Getter
@Setter
public class TenancyType implements Serializable {

    private static final long serialVersionUID = 8230787643243488944L;

    @NotEmpty(message = "{error.name.required}")
    private String name;

    @AssertTrue(message = "{error.name.invalid}")
    @JsonIgnore
    public boolean isNameConfirmationValid() {
        return !Arrays.asList(Tenancy.getDefaultNames()).contains(name.toLowerCase());
    }
}
