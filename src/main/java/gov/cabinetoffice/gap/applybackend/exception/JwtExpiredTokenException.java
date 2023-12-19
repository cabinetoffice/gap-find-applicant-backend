package gov.cabinetoffice.gap.applybackend.exception;

public class JwtExpiredTokenException extends RuntimeException {

    public JwtExpiredTokenException(String message) {
        super(message);
    }

}