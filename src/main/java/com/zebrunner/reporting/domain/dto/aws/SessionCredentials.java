package com.zebrunner.reporting.domain.dto.aws;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * An entity of minimal requirements for Amazon S3 integration
 */
@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class SessionCredentials {

    private final String accessKeyId;
    private final String secretAccessKey;
    private final String sessionToken;
    private final String region;
    private final String bucket;

}
