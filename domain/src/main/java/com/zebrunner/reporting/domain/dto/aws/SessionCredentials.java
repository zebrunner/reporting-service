package com.zebrunner.reporting.domain.dto.aws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * An entity of minimal requirements for Amazon S3 integration
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionCredentials implements Serializable {

    private static final long serialVersionUID = -2399949213318100097L;

    private String accessKeyId;
    private String secretAccessKey;
    private String sessionToken;
    private String region;
    private String bucket;

}
