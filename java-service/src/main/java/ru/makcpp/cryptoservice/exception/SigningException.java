package ru.makcpp.cryptoservice.exception;

import org.jetbrains.annotations.NotNull;

public class SigningException extends CryptoServiceException {
    public SigningException(@NotNull String message) {
        super(message);
    }

    public SigningException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
