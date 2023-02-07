package gov.cabinetoffice.gap.applybackend.exception;

public class SubmissionAlreadySubmittedException extends RuntimeException {
    public SubmissionAlreadySubmittedException(String message) {
        super(message);
    }
}
