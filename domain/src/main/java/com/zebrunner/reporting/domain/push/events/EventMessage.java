package com.zebrunner.reporting.domain.push.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class EventMessage implements Serializable {

    private static final long serialVersionUID = 2241656564064701459L;

    private String tenantName;

}
