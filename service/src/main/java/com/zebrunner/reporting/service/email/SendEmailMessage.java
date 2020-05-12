package com.zebrunner.reporting.service.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailMessage {

    private EmailType emailType;
    private String tenant;
    private String to;
    private String token;

}
