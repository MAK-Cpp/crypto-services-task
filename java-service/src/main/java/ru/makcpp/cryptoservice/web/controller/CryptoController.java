package ru.makcpp.cryptoservice.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CryptoController {
    private static final String API = "/api";
    private static final String CRYPTO_API = API + "/crypto";

    public static final String SIGN_DOCUMENT = CRYPTO_API + "/sign_document";

    @PostMapping(SIGN_DOCUMENT)
    public void signDocument() {

    }
}
