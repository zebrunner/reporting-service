package com.zebrunner.reporting.web.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BatchPatchDescriptor {

    @NotEmpty
    private List<Long> ids;

    @NotEmpty
    private String operation;

    @NotEmpty
    private String value;

}
