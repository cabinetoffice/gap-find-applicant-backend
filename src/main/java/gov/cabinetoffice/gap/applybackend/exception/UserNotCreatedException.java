package gov.cabinetoffice.gap.applybackend.exception;

public class UserNotCreatedException extends RuntimeException {
    public UserNotCreatedException(String message) {
        super(message);
    }
}