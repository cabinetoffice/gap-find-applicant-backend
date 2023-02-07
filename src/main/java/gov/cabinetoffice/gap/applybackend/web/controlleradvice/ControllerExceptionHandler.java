package gov.cabinetoffice.gap.applybackend.web.controlleradvice;

import gov.cabinetoffice.gap.applybackend.exception.*;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(value = {
            HttpClientErrorException.class,
            NotFoundException.class
    })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handle404s(Exception ex, WebRequest request) {
        return ErrorMessage.builder()
                .status(HttpStatus.NOT_FOUND)
                .date(ZonedDateTime.now(clock))
                .message(ex.getMessage())
                .description(request.getDescription(true))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseBody handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return ErrorResponseBody.builder()
                .responseAccepted(Boolean.FALSE)
                .message("Validation failure")
                .errors(ex.getAllErrors()
                        .stream()
                        .map(e -> {
                            final String fieldName = e instanceof FieldError fieldError ? fieldError.getField() : e.getObjectName();
                            return Error.builder()
                                    .fieldName(fieldName)
                                    .errorMessage(e.getDefaultMessage())
                                    .build();
                        })
                        .toList()
                )
                .invalidData(ex.getBindingResult().getTarget())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            SubmissionNotReadyException.class,
    })
    public ErrorMessage handleBadRequest(RuntimeException ex, WebRequest request) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message(ex.getMessage())
                .description(request.getDescription(true))
                .build();
    }

    @ExceptionHandler(value = {
            AttachmentException.class,
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleAttachment(Exception ex) {
        return ErrorResponseBody.builder()
                .responseAccepted(Boolean.FALSE)
                .message("Validation failure")
                .errors(List.of(Error.builder().fieldName("response").errorMessage(ex.getMessage()).build()))
                .build();
    }

    @ExceptionHandler(value = {
            FileSizeLimitExceededException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleAttachmentToLarge(WebRequest request) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message("The selected file must be smaller than 300MB")
                .description(request.getDescription(true))
                .build();
    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            GrantApplicationNotPublishedException.class,
    })
    public ErrorMessage handleGrantNotPublished(GrantApplicationNotPublishedException ex, WebRequest request) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message(ex.getMessage())
                .description(request.getDescription(true))
                .code("GRANT_NOT_PUBLISHED")
                .build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            SubmissionAlreadyCreatedException.class,
    })
    public ErrorMessage handleSubmissionAlreadyCreated(RuntimeException ex, WebRequest request) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message(ex.getMessage())
                .description(request.getDescription(true))
                .code("SUBMISSION_ALREADY_CREATED")
                .build();
    }
}
