package com.zebrunner.reporting.domain.push.events;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class IntegrationSavedEventMessage extends EventMessage {

    private final Long id;
    private final String name;
    private final boolean enabled;
    private final List<IntegrationParam> params;

    @Builder
    private IntegrationSavedEventMessage(String tenantName, Long id, String name, boolean enabled, List<IntegrationParam> params) {
        super(tenantName);
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.params = params;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class IntegrationParam {

        private final Long id;
        private final String name;
        private final String value;
        private final boolean encrypted;
        private final String passwordKey;

    }

}
