package com.zebrunner.reporting.domain.push.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class MailIntegrationNotification extends EventMessage {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String fromAddress;

    public MailIntegrationNotification(String tenantName) {
        super(tenantName);
    }
}
