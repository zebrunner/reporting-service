package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.Setting;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CryptoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoService.class);

    private final SettingsService settingsService;
    private final String salt;
    private final Map<String, CryptoDriven<?>> cryptoDrivenServices;

    public CryptoService(SettingsService settingsService,
                         @Value("${crypto-salt}") String salt,
                         @Lazy Map<String, CryptoDriven<?>> cryptoDrivenServices
    ) {
        this.settingsService = settingsService;
        this.salt = salt;
        this.cryptoDrivenServices = cryptoDrivenServices;
    }

    @PostConstruct
    public void init() {
        generateKeyIfNeed();
    }

    public String encrypt(String strToEncrypt) {
        BasicTextEncryptor basicTextEncryptor = getBasicTextEncryptor();
        return basicTextEncryptor.encrypt(strToEncrypt);
    }

    public String decrypt(String strToDecrypt) {
        BasicTextEncryptor basicTextEncryptor = getBasicTextEncryptor();
        return basicTextEncryptor.decrypt(strToDecrypt);
    }

    @Transactional(readOnly = true)
    public void generateKeyIfNeed() {
        String result = getCryptoKey();
        if (StringUtils.isBlank(result)) {
            regenerateKey();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void regenerateKey() {
        String key = null;
        String cryptoKeyType = getCryptoKeyType();
        int cryptoKeySize = getCryptoKeySize();
        try {
            key = Base64.getEncoder().encodeToString(generateKey(cryptoKeyType, cryptoKeySize).getEncoded());
        } catch (Exception e) {
            LOGGER.error("Unable to generate key: " + e.getMessage());
        }
        Setting keySetting = settingsService.getSettingByName("KEY");
        keySetting.setValue(key);
        settingsService.updateSetting(keySetting);
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public void reencrypt() {
        Map<String, Collection<?>> tempMap = new HashMap<>(collectCollectionsToReencrypt());
        tempMap.forEach(this::decryptCryptoDrivenServiceCollection);
        regenerateKey();
        tempMap.forEach((serviceName, collection) -> {
            encryptCryptoDrivenServiceCollection(serviceName, collection);
            CryptoDriven cryptoDrivenService = cryptoDrivenServices.get(serviceName);
            cryptoDrivenService.afterReencryptOperation(collection);
        });
    }

    private Map<String, Collection<?>> collectCollectionsToReencrypt() {
        return cryptoDrivenServices.entrySet().stream()
                                   .collect(Collectors.toMap(Map.Entry::getKey, cryptoDrivenEntry -> cryptoDrivenEntry.getValue().getEncryptedCollection()));
    }

    @SuppressWarnings("unchecked")
    private void decryptCryptoDrivenServiceCollection(String serviceName, Collection<?> collection) {
        CryptoDriven cryptoDrivenService = cryptoDrivenServices.get(serviceName);
        collection.forEach(o -> {
            String encryptedValue = cryptoDrivenService.getEncryptedValue(o);
            String decryptedValue = decrypt(encryptedValue);
            cryptoDrivenService.setEncryptedValue(o, decryptedValue);
        });
    }

    @SuppressWarnings("unchecked")
    private void encryptCryptoDrivenServiceCollection(String serviceName, Collection<?> collection) {
        CryptoDriven cryptoDrivenService = cryptoDrivenServices.get(serviceName);
        collection.forEach(o -> {
            String decryptedValue = cryptoDrivenService.getEncryptedValue(o);
            String encryptedValue = encrypt(decryptedValue);
            cryptoDrivenService.setEncryptedValue(o, encryptedValue);
        });
    }

    private static SecretKey generateKey(String keyType, int size) throws NoSuchAlgorithmException {
        LOGGER.debug("generating key use algorithm: '" + keyType + "'; size: " + size);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyType);
        keyGenerator.init(size);
        return keyGenerator.generateKey();
    }

    private BasicTextEncryptor getBasicTextEncryptor() {
        String key = getCryptoKey();
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(key + salt);
        return encryptor;
    }

    private String getCryptoKey() {
        Setting setting = settingsService.getSettingByName("KEY");
        return setting.getValue();
    }

    private String getCryptoKeyType() {
        Setting setting = settingsService.getSettingByName("CRYPTO_KEY_TYPE");
        return setting.getValue();
    }

    private int getCryptoKeySize() {
        Setting setting = settingsService.getSettingByName("CRYPTO_KEY_SIZE");
        return Integer.parseInt(setting.getValue());
    }

}
