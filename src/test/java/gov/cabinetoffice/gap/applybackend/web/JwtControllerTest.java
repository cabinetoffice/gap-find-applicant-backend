package gov.cabinetoffice.gap.applybackend.web;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.interfaces.DecodedJWT;
import gov.cabinetoffice.gap.applybackend.dto.api.IsAdminJwtResponse;
import gov.cabinetoffice.gap.applybackend.dto.api.IsJwtValidResponse;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.JwtTokenUndefinedException;
import gov.cabinetoffice.gap.applybackend.models.TestDecodedJwt;
import gov.cabinetoffice.gap.applybackend.service.JwtService;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
class JwtControllerTest {
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtController controllerUnderTest;

    @Test
    void validateToken_ReturnsExpectedResponse_ReturnTrue() throws JwkException {
        LocalDateTime ldt = LocalDateTime.now();
        Date date = Date.from(ldt.toInstant(ZoneOffset.UTC));

        final TestDecodedJwt decodeJwt = new TestDecodedJwt();
        decodeJwt.setExpiresAt(date);

        when(jwtService.decodedJwt("testTest"))
                .thenReturn(decodeJwt);

        when(jwtService.verifyToken(decodeJwt))
                .thenReturn(true);

        ResponseEntity<IsJwtValidResponse> response = controllerUnderTest.validateToken("bearer testTest");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().isValid()).isTrue();
        assertThat((response.getBody().getExpiresAt())).isEqualTo(date);
    }

    @Test
    void validateToken_ReturnsExpectedResponse_ReturnFalse() throws JwkException, ParseException {
        ZonedDateTime zdt = ZonedDateTime.now();
        Date date = Date.from(zdt.toInstant());
        Instant i = date.toInstant();

        final TestDecodedJwt decodeJwt = new TestDecodedJwt();
        decodeJwt.setExpiresAt(date);

        when(jwtService.decodedJwt("testTest"))
                .thenReturn(decodeJwt);

        when(jwtService.verifyToken(decodeJwt))
                .thenReturn(false);

        ResponseEntity<IsJwtValidResponse> response = controllerUnderTest.validateToken("bearer testTest");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().isValid()).isFalse();
    }

    @Test
    void validateToken_ThrowsJwtTokenUndefinedException() {
        final String emptyJwt = "";
        assertThrows(JwtTokenUndefinedException.class, () -> controllerUnderTest.validateToken(emptyJwt));
    }

    @Test
    void decodeTheTokenPayloadInAReadableFormat_ReturnsTheJwtPayload() {
        DecodedJWT decodedJWT = jwtService.decodedJwt("testTest");
        JwtPayload responseBody = JwtPayload.builder().build();
        when(jwtService.decodeTheTokenPayloadInAReadableFormat(decodedJWT))
                .thenReturn(responseBody);

        ResponseEntity<JwtPayload> response = controllerUnderTest.decodeTheTokenPayloadInAReadableFormat("testTest");


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseBody, response.getBody());
    }

    @Test
    void decodeTheTokenPayloadInAReadableFormat_ThrowsJwtTokenUndefinedException() {
        final String emptyJwt = "";
        assertThrows(JwtTokenUndefinedException.class, () -> controllerUnderTest.decodeTheTokenPayloadInAReadableFormat(emptyJwt));
    }

    @Test
    void isAdmin_ReturnsExpectedResponse() throws JwkException {

        final String token = "a test token";
        final String normalisedJwt = token.split(" ")[1];

        final JwtPayload expectedPayload = JwtPayload.builder()
                .features("{user=administrator: true, user=ordinary_user: true}")
                .build();

        final IsAdminJwtResponse expectedResponse = IsAdminJwtResponse.builder()
                .isValid(true)
                .isAdmin(true)
                .isApplicant(true)
                .build();

        when(jwtService.decodedJwt(normalisedJwt))
                .thenReturn(new TestDecodedJwt());

        when(jwtService.verifyToken(Mockito.any()))
                .thenReturn(true);

        when(jwtService.decodeTheTokenPayloadInAReadableFormat(Mockito.any()))
                .thenReturn(expectedPayload);

        final ResponseEntity<IsAdminJwtResponse> methodResponse = controllerUnderTest.isAdmin(token);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void isAdmin_ThrowsJwtTokenUndefinedException() {
        final String emptyJwt = "";
        assertThrows(JwtTokenUndefinedException.class, () -> controllerUnderTest.isAdmin(emptyJwt));
    }
}


