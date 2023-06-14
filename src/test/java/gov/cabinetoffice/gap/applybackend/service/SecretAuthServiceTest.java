package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringJUnitConfig
class SecretAuthServiceTest {

    @InjectMocks
    private SecretAuthService secretAuthService;

    private final String correctSecretKey = "topSecretKey";

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(secretAuthService, "lambdaSecret", correctSecretKey);
    }

    @Test
    void authenticateSecret_HappyPath() {
        assertThatNoException().isThrownBy(() -> secretAuthService.authenticateSecret(correctSecretKey));
    }

    @Test
    void authenticateSecret_UnhappyPath() {
        assertThatThrownBy(() -> secretAuthService.authenticateSecret("wrongKey"))
                .isInstanceOf(UnauthorizedException.class).hasMessage("Secret key does not match");
    }

}