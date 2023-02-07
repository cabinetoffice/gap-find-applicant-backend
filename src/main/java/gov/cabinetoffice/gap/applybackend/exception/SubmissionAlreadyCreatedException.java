package gov.cabinetoffice.gap.applybackend.exception;

public class SubmissionAlreadyCreatedException extends RuntimeException {
    public SubmissionAlreadyCreatedException(String message) {
        super(message);
    }
}
