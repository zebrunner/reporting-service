package com.zebrunner.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobResult {

    private String queueItemUrl;
    private boolean success;

}
