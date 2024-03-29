package gov.cabinetoffice.gap.applybackend.web;

import com.auth0.jwt.interfaces.DecodedJWT;
import gov.cabinetoffice.gap.applybackend.dto.api.IsAdminJwtResponse;
import gov.cabinetoffice.gap.applybackend.dto.api.IsJwtValidResponse;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.JwtTokenUndefinedException;
import gov.cabinetoffice.gap.applybackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/jwt")
public class JwtController {

    private final JwtService jwtService;

    @PostMapping("/isValid")
    public ResponseEntity<IsJwtValidResponse> validateToken(@RequestHeader("Authorization") String jwtToken) throws JwtTokenUndefinedException {
        if (jwtToken.length() <= 0) {
            throw new JwtTokenUndefinedException("No Jwt has been passed in the request");
        }

        final String normalisedJwt = jwtToken.split(" ")[1];
        final DecodedJWT jwt = jwtService.decodedJwt(normalisedJwt);
        final boolean isValid = jwtService.verifyToken(normalisedJwt);
        log.info("is token valid: " + isValid);

        return ResponseEntity.ok(
                IsJwtValidResponse.builder()
                        .isValid(isValid)
                        .expiresAt(jwt.getExpiresAt())
                        .build()
        );
    }

    @GetMapping("/readablePayload")
    public ResponseEntity<JwtPayload> decodeTheTokenPayloadInAReadableFormat(
            @RequestHeader("Authorization") String jwtToken) {

        if (jwtToken.length() <= 0) {
            throw new JwtTokenUndefinedException("No Jwt has been passed in the request");
        }

        final DecodedJWT jwt = jwtService.decodedJwt(jwtToken);

        return ResponseEntity.ok(jwtService.decodeTheTokenPayloadInAReadableFormat(jwt));
    }

    @GetMapping("/isAdmin")
    public ResponseEntity<IsAdminJwtResponse> isAdmin(@RequestHeader("Authorization") String jwtToken)
            throws JwtTokenUndefinedException {

        if (jwtToken.length() <= 0) {
            throw new JwtTokenUndefinedException("No Jwt has been passed in the request");
        }

        final String normalisedJwt = jwtToken.split(" ")[1];
        final DecodedJWT jwt = jwtService.decodedJwt(normalisedJwt);
        final boolean isValid = jwtService.verifyToken(normalisedJwt);
        final JwtPayload payload = jwtService.decodeTheTokenPayloadInAReadableFormat(jwt);

        final boolean isAdministrator = payload.getFeatures().contains("user=administrator");
        final boolean isOrdinaryUser = payload.getFeatures().contains("user=ordinary_user");

        final IsAdminJwtResponse response = IsAdminJwtResponse.builder()
                .isValid(isValid)
                .isAdmin(isAdministrator)
                .isApplicant(isOrdinaryUser)
                .build();

        return ResponseEntity.ok(response);
    }
}