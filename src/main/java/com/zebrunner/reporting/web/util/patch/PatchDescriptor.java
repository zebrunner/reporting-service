package com.zebrunner.reporting.web.util.patch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchDescriptor {

    @NotEmpty
    private String operation;

    @NotEmpty
    private String value;
}
