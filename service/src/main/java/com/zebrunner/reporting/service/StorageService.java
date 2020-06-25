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
     * Gets input stream by key from underlying storage
     *
     * @param key that uniquely identifies object in storage
     * @return input stream of defined resource
     */
    BinaryObject get(String key);

    /**
     * Deletes object with provided key from underlying storage
     *
     * @param key key identifying the object to be deleted
     */
    void remove(String key);

    @Deprecated
    SessionCredentials getTemporarySessionCredentials();

}
