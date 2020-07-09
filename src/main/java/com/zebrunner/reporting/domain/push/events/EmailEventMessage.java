package com.zebrunner.reporting.domain.push.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailEventMessage extends EventMessage {

    private static final long serialVersionUID = -4025219522496590801L;

    private String email;

    public EmailEventMessage(String tenancy) {
        super(tenancy);
    }

}
