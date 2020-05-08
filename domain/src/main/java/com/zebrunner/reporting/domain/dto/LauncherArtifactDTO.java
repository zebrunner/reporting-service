package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class LauncherArtifactDTO extends AbstractType {

    private String name;
    private String link;
    private Date expiresIn;

}
