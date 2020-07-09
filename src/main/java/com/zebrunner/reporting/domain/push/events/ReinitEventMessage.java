package com.zebrunner.reporting.domain.push.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReinitEventMessage extends EventMessage {

    private static final long serialVersionUID = 5300913238106855128L;

    private Long integrationId;

    public ReinitEventMessage(String tenancy, Long integrationId) {
        super(tenancy);
        this.integrationId = integrationId;
    }

}
