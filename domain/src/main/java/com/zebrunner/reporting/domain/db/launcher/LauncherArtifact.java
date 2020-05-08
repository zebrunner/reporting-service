package com.zebrunner.reporting.domain.db.launcher;

import com.zebrunner.reporting.domain.db.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class LauncherArtifact extends AbstractEntity {

    private String name;
    private String link;
    private Date expiresAt;

}
