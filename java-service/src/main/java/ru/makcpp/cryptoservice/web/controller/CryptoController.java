package ru.makcpp.cryptoservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.makcpp.cryptoservice.service.CryptoService;
import ru.makcpp.cryptoservice.web.request.SignDocumentRequest;

@RestController
@RequiredArgsConstructor
public class CryptoController {
    private static final String API = "/api";
    private static final String CRYPTO_API = API + "/crypto";

    public static final String SIGN_DOCUMENT = CRYPTO_API + "/sign_document";

    private final CryptoService cryptoService;

    @PostMapping(SIGN_DOCUMENT)
    public void signDocument(@RequestBody @NotNull SignDocumentRequest signDocumentRequest) {

    }
}
