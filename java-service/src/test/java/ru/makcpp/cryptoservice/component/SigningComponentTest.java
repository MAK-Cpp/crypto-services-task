package ru.makcpp.cryptoservice.component;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.makcpp.cryptoservice.config.KeyStoreConfig;
import ru.makcpp.cryptoservice.enumeration.KeyStoreType;
import ru.makcpp.cryptoservice.enumeration.SignType;

import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SigningComponentTest {
    private static final byte[] DOCUMENT = "document content".getBytes(StandardCharsets.UTF_8);
    private static final String KEY_STORE_PASSWORD = "changeit";
    private static final String KEY_STORE_ALIAS = "document-sign-key";

    @TempDir
    private Path tempDir;

    private SigningComponent component;

    @BeforeAll
    static void setUpProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        KeyStoreConfig keyStoreConfig = new KeyStoreConfig(
                createKeyStore(),
                KEY_STORE_PASSWORD,
                KeyStoreType.PKCS12,
                KEY_STORE_ALIAS
        );

        component = new SigningComponent(keyStoreConfig);
    }

    @Nested
    class Positive {
        @ParameterizedTest
        @DisplayName("Should sign document")
        @EnumSource(SignType.class)
        public void testSignDocument(SignType signType) {
            byte[] signature = assertDoesNotThrow(() -> component.signDocument(DOCUMENT, signType));

            assertNotNull(signature);
            assertTrue(signature.length > 0);
            assertValidCmsSignature(signature, signType);
        }
    }

    private static void assertValidCmsSignature(byte[] signature, SignType signType) {
        assertDoesNotThrow(() -> {
            CMSSignedData signedData = parseSignedData(signature, signType);
            Collection<X509CertificateHolder> certificates = signedData.getCertificates().getMatches(null);
            Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();

            assertFalse(certificates.isEmpty());
            assertEquals(1, signers.size());

            X509CertificateHolder certificate = certificates.iterator().next();
            SignerInformation signer = signers.iterator().next();
            SignerInformationVerifier verifier = new JcaSimpleSignerInfoVerifierBuilder()
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    .build(certificate);

            assertTrue(signer.verify(verifier));

            if (signType == SignType.ATTACHED) {
                assertArrayEquals(DOCUMENT, (byte[]) signedData.getSignedContent().getContent());
            }
        });
    }

    private static CMSSignedData parseSignedData(byte[] signature, SignType signType) throws Exception {
        return switch (signType) {
            case ATTACHED -> new CMSSignedData(signature);
            case DETACHED -> new CMSSignedData(new CMSProcessableByteArray(DOCUMENT), signature);
        };
    }

    private Path createKeyStore() throws Exception {
        KeyPair keyPair = generateKeyPair();
        X509Certificate certificate = generateCertificate(keyPair);
        KeyStore keyStore = KeyStore.getInstance(KeyStoreType.PKCS12.name());
        char[] password = KEY_STORE_PASSWORD.toCharArray();

        keyStore.load(null, password);
        keyStore.setKeyEntry(KEY_STORE_ALIAS, keyPair.getPrivate(), password, new X509Certificate[]{certificate});

        Path keyStorePath = tempDir.resolve("test-keystore.p12");
        try (OutputStream outputStream = Files.newOutputStream(keyStorePath)) {
            keyStore.store(outputStream, password);
        }

        return keyStorePath;
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static X509Certificate generateCertificate(KeyPair keyPair) throws Exception {
        Instant now = Instant.now();
        X500Name subject = new X500Name("CN=SigningComponentTest");
        X509CertificateHolder holder = new JcaX509v3CertificateBuilder(
                subject,
                BigInteger.valueOf(new SecureRandom().nextLong(1, Long.MAX_VALUE)),
                Date.from(now.minusSeconds(60)),
                Date.from(now.plusSeconds(3600)),
                subject,
                keyPair.getPublic()
        ).build(new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build(keyPair.getPrivate()));

        return new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(holder);
    }
}
