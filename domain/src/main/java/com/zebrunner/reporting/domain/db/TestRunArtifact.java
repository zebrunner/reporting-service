package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class TestRunArtifact implements Serializable {

    private static final long serialVersionUID = 4940282315917013242L;

    private Long id;
    private String name;
    private String link;
    private LocalDateTime expiresAt;
    private Long testRunId;

    public boolean isValid() {
        return name != null && !name.isEmpty() && link != null && !link.isEmpty();
    }

}