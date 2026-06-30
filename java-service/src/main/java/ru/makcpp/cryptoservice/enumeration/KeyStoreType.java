package ru.makcpp.cryptoservice.enumeration;

import lombok.RequiredArgsConstructor;
import ru.makcpp.cryptoservice.exception.InternalException;

import java.security.KeyStore;
import java.security.KeyStoreException;

@RequiredArgsConstructor
public enum KeyStoreType {
    PKCS12("PKCS12"),
    ;

    private final String type;

    public KeyStore getKeyStore() {
        try {
            return KeyStore.getInstance(type);
        } catch (KeyStoreException e) {
            throw new InternalException("Unexpected exception while getting key store", e);
        }
    }
}
