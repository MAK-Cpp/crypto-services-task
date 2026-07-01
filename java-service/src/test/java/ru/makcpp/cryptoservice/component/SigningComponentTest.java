package ru.makcpp.cryptoservice.component;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.makcpp.cryptoservice.config.KeyStoreConfig;

@ExtendWith(MockitoExtension.class)
class SigningComponentTest {
    @Mock
    private KeyStoreConfig keyStoreConfig;

    @InjectMocks
    private SigningComponent component;

    @Nested
    class Positive {
        
    }
}