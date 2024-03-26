package gov.cabinetoffice.gap.applybackend.web.controlleradvice;

import gov.cabinetoffice.gap.applybackend.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    public static final List<String> ADDRESS_FIELDS = List.of("addressLine1", "addressLine2", "city", "county", "postcode");

    private final Clock clock;

    @ExceptionHandler(value = {
            HttpClientErrorException.class,
            NotFoundException.class
    })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handle404s(Exception ex, WebRequest request) {
        log.error("Exception thrown: ", ex.getStackTrace());
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
        log.error("MethodArgumentNotValidException thrown: ", ex.getStackTrace());
        final List<Error> errors = ex.getAllErrors()
                .stream()
                .map(e -> {
                    final String fieldName = e instanceof FieldError fieldError ? fieldError.getField() : e.getObjectName();
                    return Error.builder()
                            .fieldName(fieldName)
                            .errorMessage(e.getDefaultMessage())
                            .build();
                })
                .toList();

        return ErrorResponseBody.builder()
                .responseAccepted(Boolean.FALSE)
                .message("Validation failure")
                .errors(sortErrors(errors))
                .invalidData(ex.getBindingResult().getTarget())
                .build();
    }

    private Optional<Function<Error, Integer>> getErrorIntegerFunction(List<Error> errors) {
        final boolean isMultiResponse = errors.stream()
                .anyMatch(error -> error.getFieldName().contains("multiResponse"));
        final boolean isAddressResponse = errors.stream()
                .anyMatch(error -> ADDRESS_FIELDS.contains(error.getFieldName()));

        if (isMultiResponse)
            return Optional.of(this::getIntegerFromFieldName);
        else if (isAddressResponse)
            return Optional.of(this::getIntFromAddressField);

        return Optional.empty();
    }

    private List<Error> sortErrors(List<Error> errors) {
        return getErrorIntegerFunction(errors)
                .stream()
                .map(fn -> errors.stream()
                            .sorted((a, b) -> {
                                int multiResponseField = fn.apply(a);
                                int multiResponseField2 = fn.apply(b);

                                return multiResponseField - multiResponseField2;
                            })
                            .toList()
                )
                .findFirst()
                .orElse(errors);
    }

    private int getIntFromAddressField(Error fieldError) {
        switch (fieldError.getFieldName()) {
            case "addressLine1":
                return 1;
            case "addressLine2":
                return 2;
            case "city":
                return 3;
            case "county":
                return 4;
            case "postcode":
                return 5;
            default:
                return 0;
        }
    }

    private int getIntegerFromFieldName(Error fieldError) {
        final Pattern pattern = Pattern.compile("-?\\d+");
        final Matcher matcher = pattern.matcher(fieldError.getFieldName());

        while (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group());
            } catch (NumberFormatException e) {
                log.error("can't parse string as integer", e);
                return 0;
            }
        }

        return 0;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            SubmissionNotReadyException.class,
    })
    public ErrorMessage handleBadRequest(RuntimeException ex, WebRequest request) {
        log.error("SubmissionNotReadyException thrown: ", ex.getStackTrace());
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
        log.error("AttachmentException thrown: ", ex.getStackTrace());
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
        log.error("GrantApplicationNotPublishedException thrown: ", ex.getStackTrace());
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
    public ErrorMessage handleSubmissionAlreadyCreated(SubmissionAlreadyCreatedException ex, WebRequest request) {
        log.error("SubmissionAlreadyCreatedException thrown: ", ex.getStackTrace());
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message(ex.getMessage())
                .description(request.getDescription(true))
                .code("SUBMISSION_ALREADY_CREATED")
                .build();
    }
}
