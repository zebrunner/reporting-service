package com.zebrunner.reporting.service.impl;

import com.zebrunner.reporting.domain.dto.aws.FileUploadType;
import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.service.S3Properties;
import com.zebrunner.reporting.service.StorageService;
import com.zebrunner.reporting.service.util.URLResolver;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final S3Client s3Client;
    private final StsClient stsClient;
    private final S3Properties s3Properties;
    private final URLResolver urlResolver;

    @Value("${service.multitenant}")
    private boolean multitenant;

    @Value("${amazon-token-expiration}")
    private Integer expiresInSec;

    @Override
    public String saveObject(FileUploadType file) {
        String relativePath = getFileKey(file);
        String key = TenancyContext.getTenantName() + relativePath;

        ObjectCannedACL acl = multitenant ? ObjectCannedACL.PRIVATE : ObjectCannedACL.PUBLIC_READ;

        s3Client.putObject(
                rb -> rb.bucket(s3Properties.getBucket()).key(key).acl(acl).build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getFileSize())
        );

        return urlResolver.getServiceURL();
    }

    private String getFileKey(FileUploadType file) {
        return file.getType().getPath() + "/" + RandomStringUtils.randomAlphanumeric(20) + "." +
                FilenameUtils.getExtension(file.getFileName());
    }

    @Override
    public void removeObject(String key) {
        s3Client.deleteObject(rb -> rb.bucket(s3Properties.getBucket()).key(key).build());
    }

    public SessionCredentials getTemporarySessionCredentials() {
        GetSessionTokenResponse response = stsClient.getSessionToken(rb -> rb.durationSeconds(expiresInSec).build());
        Credentials credentials = response.credentials();
        return SessionCredentials.builder()
                                 .accessKeyId(credentials.accessKeyId())
                                 .secretAccessKey(credentials.secretAccessKey())
                                 .bucket(s3Properties.getBucket())
                                 .region(s3Properties.getRegion())
                                 .sessionToken(credentials.sessionToken())
                                 .build();
    }

}
