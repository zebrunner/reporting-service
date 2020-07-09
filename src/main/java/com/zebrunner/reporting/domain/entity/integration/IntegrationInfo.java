package com.zebrunner.reporting.domain.entity.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntegrationInfo {

    private final Long integrationId;
    private final String integrationBackReferenceId;
    private final boolean isDefault;
    private final boolean connected;
    private final boolean enabled;
}
