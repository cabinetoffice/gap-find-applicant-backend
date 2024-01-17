package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SecretAuthService {

    @Value("${lambda.secret}")
    private String lambdaSecret;

    /**
     * Intended to authenticate requests coming from lambdas, which shouldn't pass through
     * the JWT auth process. TODO I think this could be converted into an annotation for
     * lambda controller methods
     *
     * @param authHeader value taken from Authorization header
     */
    public void authenticateSecret(String authHeader) {
        if (!Objects.equals(lambdaSecret, authHeader)) {
            throw new UnauthorizedException("Secret key does not match");
        }
    }

}