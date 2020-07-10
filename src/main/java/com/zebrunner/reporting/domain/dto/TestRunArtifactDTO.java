package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
public class TestRunArtifactDTO  implements Serializable {

    private static final long serialVersionUID = 3099440718828716032L;

    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String link;
    private Long testRunId;
    private Integer expiresIn;

}