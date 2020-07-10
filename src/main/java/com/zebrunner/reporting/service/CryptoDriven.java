package com.zebrunner.reporting.service;

import java.util.Collection;

public interface CryptoDriven<T> {

    Collection<T> getEncryptedCollection();

    void afterReencryptOperation(Collection<T> reencryptedCollection);

    String getEncryptedValue(T entity);

    void setEncryptedValue(T entity, String encryptedString);

}
