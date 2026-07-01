package ru.makcpp.cryptoservice.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalStateException extends CryptoServiceException {
    public IllegalStateException(@NotNull String message) {
        super(message);
    }

    public IllegalStateException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
