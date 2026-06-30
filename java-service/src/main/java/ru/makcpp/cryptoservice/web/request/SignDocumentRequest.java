package ru.makcpp.cryptoservice.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.makcpp.cryptoservice.enumeration.SignType;

public record SignDocumentRequest(
        @JsonProperty(value = "sign_type", required = true)
        SignType signType
) {
}
