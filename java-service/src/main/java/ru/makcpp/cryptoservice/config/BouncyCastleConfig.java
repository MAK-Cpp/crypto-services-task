package ru.makcpp.cryptoservice.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Slf4j
@Configuration
public class BouncyCastleConfig {
    @PostConstruct
    public void registerBouncyCastleProvider() {
        log.info("Registering BouncyCastle provider");
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
            log.info("BouncyCastle provider registered successfully");
        } else {
            log.info("BouncyCastle provider already registered");
        }
    }
}
