package ru.makcpp.cryptoservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import ru.makcpp.cryptoservice.enumeration.KeyStoreType;
import ru.makcpp.cryptoservice.validation.annotation.RegularFile;

import java.nio.file.Path;

@Validated
@ConfigurationProperties(prefix = "crypto.key-store", ignoreUnknownFields = false)
public record KeyStoreConfig(
        @NotNull
        @RegularFile
        Path path,

        @NotBlank
        String password,

        @NotNull
        KeyStoreType type
) {
}
