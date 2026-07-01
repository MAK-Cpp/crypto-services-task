package ru.makcpp.cryptoservice.component;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.makcpp.cryptoservice.config.KeyStoreConfig;
import ru.makcpp.cryptoservice.enumeration.SignType;
import ru.makcpp.cryptoservice.exception.InternalException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

@Slf4j
@Component
public class SigningComponent {
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private final PrivateKey privateKey;
    private final X509Certificate certificate;

    @Autowired
    public SigningComponent(KeyStoreConfig config) {
        KeyStore keyStore = config.type().getKeyStore();
        char[] password = config.password().toCharArray();
        log.debug("password length = {}", config.password().length());
        log.debug("keystore path = {}", config.path());
        log.debug("alias = {}", config.alias());

        try (InputStream is = Files.newInputStream(config.path())) {
            keyStore.load(is, password);
            privateKey = (PrivateKey) keyStore.getKey(config.alias(), password);
            certificate = (X509Certificate) keyStore.getCertificate(config.alias());
        } catch (Exception e) {
            throw new InternalException("Error while getting private key and certificate", e);
        }
    }

    private ContentSigner createContentSigner() {
        try {
            return new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    .build(privateKey);
        } catch (OperatorCreationException e) {
            throw new RuntimeException(e);
        }
    }

    private void addContentSigner(@NotNull CMSSignedDataGenerator generator,
                                  @NotNull ContentSigner contentSigner) {
        try {
            generator.addSignerInfoGenerator(
                    new JcaSignerInfoGeneratorBuilder(
                            new JcaDigestCalculatorProviderBuilder()
                                    .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                                    .build()
                    ).build(contentSigner, certificate)
            );
        } catch (OperatorCreationException e) {
            throw new RuntimeException(e);
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addCertificate(@NotNull CMSSignedDataGenerator generator) {
        try {
            generator.addCertificates(new JcaCertStore(List.of(certificate)));
        } catch (CMSException e) {
            throw new RuntimeException(e);
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private @NotNull CMSSignedData generateSignedData(@NotNull CMSSignedDataGenerator generator,
                                                      @NotNull CMSProcessableByteArray content,
                                                      @NotNull SignType signType) {
        try {
            return switch (signType) {
                case ATTACHED -> generator.generate(content, true);
                case DETACHED -> generator.generate(content, false);
            };
        } catch (CMSException e) {
            throw new RuntimeException(e);
        }
    }

    public byte @NotNull [] signDocument(byte @NotNull [] document,
                                         @NotNull SignType signType) {
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

        ContentSigner contentSigner = createContentSigner();

        addContentSigner(generator, contentSigner);
        addCertificate(generator);

        CMSProcessableByteArray content = new CMSProcessableByteArray(document);
        CMSSignedData signedData = generateSignedData(generator, content, signType);

        try {
            return signedData.getEncoded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
