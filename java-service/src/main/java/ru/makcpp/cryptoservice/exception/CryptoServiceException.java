package ru.makcpp.cryptoservice.exception;

import org.jetbrains.annotations.NotNull;

public abstract class CryptoServiceException extends RuntimeException {
    public CryptoServiceException(@NotNull String message) {
        super(message);
    }

    public CryptoServiceException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
