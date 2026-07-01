package ru.makcpp.cryptoservice.exception;

import org.jetbrains.annotations.NotNull;

public class SigningMaterialException extends CryptoServiceException {
    public SigningMaterialException(@NotNull String message) {
        super(message);
    }

    public SigningMaterialException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
