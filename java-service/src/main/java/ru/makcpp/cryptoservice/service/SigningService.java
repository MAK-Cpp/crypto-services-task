package ru.makcpp.cryptoservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.makcpp.cryptoservice.config.KeyStoreConfig;
import ru.makcpp.cryptoservice.exception.InternalException;

import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Slf4j
@Service
public class SigningService {
    private final PrivateKey privateKey;
    private final X509Certificate certificate;

    @Autowired
    public SigningService(KeyStoreConfig config) {
        KeyStore keyStore = config.type().getKeyStore();
        char[] password = config.password().toCharArray();
        log.debug("password length = {}", config.password().length());
        log.debug("keystore path = {}", config.path());
        log.debug("alias = {}", config.alias());

        try (InputStream is = Files.newInputStream(config.path())) {
            keyStore.load(is, password);
            privateKey = (PrivateKey) keyStore.getKey(config.alias(), password);
            certificate = (X509Certificate) keyStore.getCertificate(config.alias());
        } catch (Exception e) {
            throw new InternalException("Error while getting private key and certificate", e);
        }
    }
}
