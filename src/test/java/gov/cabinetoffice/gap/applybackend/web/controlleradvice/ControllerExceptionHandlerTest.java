package gov.cabinetoffice.gap.applybackend.web.controlleradvice;

import gov.cabinetoffice.gap.applybackend.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {
    private ControllerExceptionHandler exceptionHandlerUnderTest;
    private final String CHRISTMAS_2022_MIDDAY = "2022-12-25T12:00:00.00z";
    private final Clock clock = Clock.fixed(Instant.parse(CHRISTMAS_2022_MIDDAY), ZoneId.of("UTC"));

    @BeforeEach
    void setup() {
        exceptionHandlerUnderTest = new ControllerExceptionHandler(clock);
    }

    private static Stream<Arguments> provideExceptions() {
        return Stream.of(
                Arguments.of(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Bad query")),
                Arguments.of(new NotFoundException("Not Found"))
                );
    }

    @ParameterizedTest
    @MethodSource("provideExceptions")
    void handle404s_HandlesExceptions_AndReturnsExpectedResponse(Exception ex) {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-uri");
        request.setRemoteAddr("https://some-domain.com");

        WebRequest webRequest = new ServletWebRequest(request);

        final ErrorMessage expectedErrorMessage = ErrorMessage.builder()
                .status(HttpStatus.NOT_FOUND)
                .date(ZonedDateTime.now(clock))
                .message(ex.getMessage())
                .description("uri=/some-uri;client=https://some-domain.com")
                .build();

        ErrorMessage response = exceptionHandlerUnderTest.handle404s(ex, webRequest);

        assertThat(response).isEqualTo(expectedErrorMessage);
    }

    @Test
    void handleValidationExceptions_HandlesException_AndReturnsExpectedResponse() {

        final String fieldErrorMessage = "You must enter a response";
        final String singleResponseErrorField = "response";

        final String objectErrorMessage = "You must enter an address";

        // set up validation failure
        final ObjectError fieldError = new FieldError("question", singleResponseErrorField, fieldErrorMessage);
        final ObjectError objectError = new ObjectError("question", objectErrorMessage);

        final BindingResult result = mock(BindingResult.class);
        when(result.getAllErrors())
                .thenReturn(List.of(fieldError, objectError));

        // exception thrown by Spring when validation failure occurs
        final MethodParameter parameter = mock(MethodParameter.class);
        final MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, result);

        // make the web request
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-uri");
        request.setRemoteAddr("https://some-domain.com");

        // Build the expected error message
        final Error expectedFieldError = Error.builder()
                .fieldName(singleResponseErrorField)
                .errorMessage(fieldErrorMessage)
                .build();
        final Error expectedObjectError = Error.builder()
                .fieldName("question")
                .errorMessage(objectErrorMessage)
                .build();
        final ErrorResponseBody body = ErrorResponseBody.builder()
                .responseAccepted(Boolean.FALSE)
                .message("Validation failure")
                .errors(List.of(expectedFieldError, expectedObjectError))
                .build();

        ErrorResponseBody response = exceptionHandlerUnderTest.handleValidationExceptions(ex);

        assertThat(response).isEqualTo(body);
    }

    private static Stream<Arguments> provideBadRequests() {
        return Stream.of(
                Arguments.of(new SubmissionNotReadyException("Submission is not ready")),
                Arguments.of(new SubmissionAlreadySubmittedException("Already submitted"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideBadRequests")
    void handleBadRequest_ReturnsExpectedResponse(RuntimeException ex) {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-uri");
        request.setRemoteAddr("https://some-domain.com");

        WebRequest webRequest = new ServletWebRequest(request);

        final ErrorMessage expectedErrorMessage = ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message(ex.getMessage())
                .description("uri=/some-uri;client=https://some-domain.com")
                .build();

        ErrorMessage response = exceptionHandlerUnderTest.handleBadRequest(ex, webRequest);

        assertThat(response).isEqualTo(expectedErrorMessage);
    }

    @Test
    void handleAttachment_ReturnsExpectedResponseBody() {

        final String errorMessage = "File must be of type TXT, CSV, XLSX, DOCX";
        final Error validationError = Error.builder()
                .fieldName("response")
                .errorMessage(errorMessage)
                .build();

        final ErrorResponseBody expectedResponse = ErrorResponseBody.builder()
                .errors(List.of(validationError))
                .responseAccepted(false)
                .message("Validation failure")
                .build();

        final AttachmentException ex = new AttachmentException(errorMessage);

        final ErrorResponseBody methodResponse = exceptionHandlerUnderTest.handleAttachment(ex);

        assertThat(methodResponse).isEqualTo(expectedResponse);
    }

    @Test
    void handleAttachmentToLarge_ReturnsExpectedErrorResponse() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-uri");
        request.setRemoteAddr("https://some-domain.com");

        final WebRequest webRequest = new ServletWebRequest(request);

        final String errorMessage = "File must be of type TXT, CSV, XLSX, DOCX";
        final ErrorMessage expectedError = ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message("The selected file must be smaller than 300MB")
                .description(webRequest.getDescription(true))
                .build();

        final ErrorMessage methodResponse = exceptionHandlerUnderTest.handleAttachmentToLarge(webRequest);

        assertThat(methodResponse).isEqualTo(expectedError);
    }

    @Test
    void handleGrantNotPublished_ReturnsExpectedErrorResponse() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-uri");
        request.setRemoteAddr("https://some-domain.com");

        final WebRequest webRequest = new ServletWebRequest(request);

        final String errorMessage = "This grant is not open";
        final ErrorMessage expectedError = ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message(errorMessage)
                .description(webRequest.getDescription(true))
                .code("GRANT_NOT_PUBLISHED")
                .build();

        final GrantApplicationNotPublishedException ex = new GrantApplicationNotPublishedException(errorMessage);

        final ErrorMessage methodResponse = exceptionHandlerUnderTest.handleGrantNotPublished(ex, webRequest);

        assertThat(methodResponse).isEqualTo(expectedError);
    }

    @Test
    void handleSubmissionAlreadyCreated_ReturnsExpectedErrorResponse() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-uri");
        request.setRemoteAddr("https://some-domain.com");

        final WebRequest webRequest = new ServletWebRequest(request);

        final String errorMessage = "You have already created a submission for this grant";
        final ErrorMessage expectedError = ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .date(ZonedDateTime.now(clock))
                .message(errorMessage)
                .description(webRequest.getDescription(true))
                .code("SUBMISSION_ALREADY_CREATED")
                .build();

        final SubmissionAlreadyCreatedException ex = new SubmissionAlreadyCreatedException(errorMessage);

        final ErrorMessage methodResponse = exceptionHandlerUnderTest.handleSubmissionAlreadyCreated(ex, webRequest);

        assertThat(methodResponse).isEqualTo(expectedError);
    }
}