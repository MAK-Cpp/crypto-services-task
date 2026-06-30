package ru.makcpp.cryptoservice.enumeration;

import lombok.RequiredArgsConstructor;

import java.security.KeyStore;
import java.security.KeyStoreException;

@RequiredArgsConstructor
public enum KeyStoreType {
    PKCS12("PKCS12"),
    ;

    private final String type;

    public KeyStore getKeyStore() throws KeyStoreException {
        return KeyStore.getInstance(type);
    }
}
