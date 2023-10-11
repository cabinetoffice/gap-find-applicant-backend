package gov.cabinetoffice.gap.applybackend.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    private HealthController controllerUnderTest;

    @BeforeEach
    void setup () {
        controllerUnderTest = new HealthController();
    }

    @Test
    void getHealthCheck_ReturnsExpected() {
        final ResponseEntity<String> methodResponse = controllerUnderTest.getHealthCheck();

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo("Service up");
    }

}