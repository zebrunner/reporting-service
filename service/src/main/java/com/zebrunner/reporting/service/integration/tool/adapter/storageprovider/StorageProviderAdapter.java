package com.zebrunner.reporting.service.integration.tool.adapter.storageprovider;

import com.zebrunner.reporting.domain.dto.aws.FileUploadType;
import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationGroupAdapter;

import java.util.Optional;

public interface StorageProviderAdapter extends IntegrationGroupAdapter {

    String saveFile(final FileUploadType file);

    void removeFile(final String linkToFile);

    Optional<SessionCredentials> getTemporarySessionCredentials(int expiresIn);

}
