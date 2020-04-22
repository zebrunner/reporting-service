package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
public class LauncherScannerType implements Serializable {

    private static final long serialVersionUID = 8868128477356200279L;

    @NotEmpty
    private String branch;

    @Min(1)
    private long scmAccountId;
    private boolean rescan;

}
