package com.zebrunner.reporting.domain.push.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDefaultUserMessage {

    private String tenantName;
    private String tenantUrl;
    private String email;
    private boolean success;
    private String message;

}
