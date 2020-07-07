package com.zebrunner.reporting.domain.push.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateDefaultUserMessage extends EventMessage{

    private String tenantUrl;
    private String email;
    private boolean success;
    private String message;

    public CreateDefaultUserMessage(String tenantName, String tenantUrl, String email, boolean success, String message) {
        super(tenantName);
        this.tenantUrl = tenantUrl;
        this.email = email;
        this.success = success;
        this.message = message;
    }
}
