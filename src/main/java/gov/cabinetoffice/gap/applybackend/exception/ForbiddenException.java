package gov.cabinetoffice.gap.applybackend.exception;

/**
 * Exception thrown when a user attempts to access a resource they do not have permission to access.
 * This is used to enforce object-level authorization and prevent IDOR (Insecure Direct Object Reference) vulnerabilities.
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }
    
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
