package com.zebrunner.reporting.service.impl;

import com.zebrunner.reporting.domain.dto.BinaryObject;
import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.service.S3Properties;
import com.zebrunner.reporting.service.StorageService;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private static final String[] ALLOWED_IMAGE_CONTENT_TYPES = {"image/png", "image/jpeg"};
    private static final String[] APP_EXTENSIONS = {"app", "ipa", "apk", "apks"};

    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;         // 2 MB
    private static final long MAX_APP_PACKAGE_SIZE = 100 * 1024 * 1024; // 100 MB

    private final S3Client s3Client;
    private final StsClient stsClient;
    private final S3Properties s3Properties;

    @Value("${amazon-token-expiration}")
    private Integer expiresInSec;

    @Override
    public String save(BinaryObject binaryObject) {
        validateObject(binaryObject);
        String key = buildObjectKey(binaryObject);
        s3Client.putObject(
                rb -> rb.bucket(s3Properties.getBucket()).key(key).acl(ObjectCannedACL.PRIVATE).build(),
                RequestBody.fromInputStream(binaryObject.getData(), binaryObject.getSize())
        );
        return key;
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

    private void validateObject(BinaryObject binaryObject) {
        BinaryObject.Type type = binaryObject.getType();
        switch (type) {
            case ORG_ASSET:
            case USER_ASSET:
                if (binaryObject.getSize() > MAX_IMAGE_SIZE) {
                    throw new IllegalOperationException(IllegalOperationException.IllegalOperationErrorDetail.INVALID_FILE, "File size should be less than 2MB");
                }
                if (!ArrayUtils.contains(ALLOWED_IMAGE_CONTENT_TYPES, binaryObject.getContentType())) {
                    throw new IllegalOperationException(IllegalOperationException.IllegalOperationErrorDetail.INVALID_FILE, "File should be either JPEG or PNG image");
                }
                break;
            case APP_PACKAGE:
                String extension = FilenameUtils.getExtension(binaryObject.getName());
                if (binaryObject.getSize() > MAX_APP_PACKAGE_SIZE) {
                    throw new IllegalOperationException(IllegalOperationException.IllegalOperationErrorDetail.INVALID_FILE, "File size should be less than 100MB");
                }
                if (!ArrayUtils.contains(APP_EXTENSIONS, extension)) {
                    throw new IllegalOperationException(IllegalOperationException.IllegalOperationErrorDetail.INVALID_FILE, "File should have format APP, IPA or APK");
                }
                break;
        }
    }

    private String buildObjectKey(BinaryObject binaryObject) {
        String name = UUID.randomUUID().toString();
        return TenancyContext.getTenantName() + "/"
                + getKeyPrefix(binaryObject.getType()) + "/"
                + name + "."
                + FilenameUtils.getExtension(binaryObject.getName());
    }

    private String getKeyPrefix(BinaryObject.Type type) {
        String prefix = "";
        switch (type) {
            case ORG_ASSET:
                prefix = "assets/org";
                break;
            case USER_ASSET:
                prefix = "assets/user";
                break;
            case APP_PACKAGE:
                prefix = "artifacts/applications";
                break;
        }
        return prefix;
    }

}
