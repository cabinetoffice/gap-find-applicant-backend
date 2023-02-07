package gov.cabinetoffice.gap.applybackend.web.controlleradvice;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@Builder
public class ErrorMessage {
    private HttpStatus status;
    private ZonedDateTime date;
    private String message;
    private String description;
    private ErrorResponseBody body;
    private String code;
}
