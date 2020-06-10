package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.dto.aws.FileUploadType;
import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;

public interface StorageService {

    String saveObject(FileUploadType file);

    void removeObject(String key);

    SessionCredentials getTemporarySessionCredentials();

}
