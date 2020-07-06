package com.zebrunner.reporting.service.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDefaultUserMessage {

    private String tenantName;
    private String tenantUrl;
    private String email;
    private boolean success;
    private String message;

}
