package com.zebrunner.reporting.domain.push.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenancyResponseEventMessage extends EventMessage {

    private static final long serialVersionUID = -8149563995165621982L;

    private String token;
    private String zafiraURL;
    private Boolean success;
    private String message;

    public TenancyResponseEventMessage(String tenancy) {
        super(tenancy);
    }

    public TenancyResponseEventMessage(String tenancy, String token, String zafiraURL, Boolean success) {
        super(tenancy);
        this.token = token;
        this.zafiraURL = zafiraURL;
        this.success = success;
    }

}
