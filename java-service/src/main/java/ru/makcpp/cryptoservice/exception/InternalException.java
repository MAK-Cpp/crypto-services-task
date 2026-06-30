package ru.makcpp.cryptoservice.exception;

import org.jetbrains.annotations.NotNull;

public class InternalException extends CryptoServiceException {
    public InternalException(@NotNull String message) {
        super(message);
    }

    public InternalException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
