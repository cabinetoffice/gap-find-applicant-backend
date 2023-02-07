package gov.cabinetoffice.gap.applybackend.exception;

public class JwtTokenUndefinedException extends RuntimeException {

    public JwtTokenUndefinedException(String message) {
        super(message);
    }

}
