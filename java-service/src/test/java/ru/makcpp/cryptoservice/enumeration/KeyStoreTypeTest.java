package ru.makcpp.cryptoservice.enumeration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class KeyStoreTypeTest {
    @ParameterizedTest
    @DisplayName("KeyStoreType не должен кидать исключения при вызове getKeyStore()")
    @EnumSource(KeyStoreType.class)
    public void testGetKeyStoreShouldNotThrowExceptions(KeyStoreType type) {
        Assertions.assertDoesNotThrow(type::getKeyStore);
    }
}