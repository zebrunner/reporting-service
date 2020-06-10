package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.dto.BinaryObject;
import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;

public interface StorageService {

    /**
     * Uploads given binary object to underlying storage
     *
     * @param binaryObject object to be uploaded
     * @return key than uniquely identifies object in storage
     */
    String save(BinaryObject binaryObject);

    /**
     * Deletes object with provided key from underlying storage
     *
     * @param key key identifying the object to be deleted
     */
    void removeObject(String key);

    @Deprecated
    SessionCredentials getTemporarySessionCredentials();

}
