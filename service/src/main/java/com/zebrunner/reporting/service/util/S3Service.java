package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.domain.properties.S3ClientProps;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;

@AllArgsConstructor
@Component
public class S3Service {

    private final S3Client s3Client;
    private final S3ClientProps props;

    public InputStream getObject(String key) {
        return s3Client.getObject(rb -> rb.bucket(props.getBucket()).key(key).build(), ResponseTransformer.toInputStream());
    }
}
