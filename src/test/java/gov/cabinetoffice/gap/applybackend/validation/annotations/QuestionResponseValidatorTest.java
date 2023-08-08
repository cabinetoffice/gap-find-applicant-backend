package gov.cabinetoffice.gap.applybackend.validation.annotations;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateQuestionResponseDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestion;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestionValidation;
import gov.cabinetoffice.gap.applybackend.service.SubmissionService;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionQuestionResponseType;
import gov.cabinetoffice.gap.applybackend.validation.validators.QuestionResponseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionResponseValidatorTest {

    @Mock
    private Authentication authentication;
    @Mock
    private SubmissionService submissionService;

    @Mock
    private SecurityContext securityContext;

    private ConstraintValidatorContext validatorContext;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private QuestionResponseValidator validatorUnderTest;

    static final String questionId = "1";
    static final UUID submissionId = UUID.fromString("3a6cfe2d-bf58-440d-9e07-3579c7dcf205");
    private final String USER_ID = String.valueOf(UUID.randomUUID());

    @BeforeEach
    void setup() {
        nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        validatorContext = mock(ConstraintValidatorContext.class);

        validatorUnderTest = new QuestionResponseValidator(submissionService);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        JwtPayload jwtPayload = JwtPayload.builder().sub(String.valueOf(USER_ID)).build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(

                // mandatory field missing
                Arguments.of(
                        CreateQuestionResponseDto.builder()
                                .questionId(questionId)
                                .submissionId(submissionId)
                                .build()
                        , "You must enter an answer",
                        SubmissionQuestionValidation.builder()
                                .mandatory(Boolean.TRUE)
                                .build()),

                // min length check should fail
                Arguments.of(
                        CreateQuestionResponseDto.builder()
                                .questionId(questionId)
                                .submissionId(submissionId)
                                .response("A")
                                .build()
                        , "Answer must be 2 characters or more",
                        SubmissionQuestionValidation.builder()
                                .minLength(2)
                                .build()),

                // max length check should fail
                Arguments.of(
                        CreateQuestionResponseDto.builder()
                                .questionId(questionId)
                                .submissionId(submissionId)
                                .response("Lorem ipsum dolor sit amet, consectetur erat curae.")
                                .build()
                        , "Answer must be 50 characters or less",
                        SubmissionQuestionValidation.builder()
                                .maxLength(50)
                                .build()),

                // min word check should fail
                Arguments.of(
                        CreateQuestionResponseDto.builder()
                                .questionId(questionId)
                                .submissionId(submissionId)
                                .response("Oneword")
                                .build()
                        , "Answer must be 2 words or more",
                        SubmissionQuestionValidation.builder()
                                .minWords(2)
                                .build()),

                // max word check should fail
                Arguments.of(
                        CreateQuestionResponseDto.builder()
                                .questionId(questionId)
                                .submissionId(submissionId)
                                .response("Lorum Ipsum Dolor")
                                .build()
                        , "Answer must be 2 words or less",
                        SubmissionQuestionValidation.builder()
                                .maxWords(2)
                                .build()),

                // getGreaterThan0 check should fail
                Arguments.of(
                        CreateQuestionResponseDto.builder()
                                .questionId(questionId)
                                .submissionId(submissionId)
                                .response("0")
                                .build()
                        , "Answer must have a value greater than zero",
                        SubmissionQuestionValidation.builder()
                                .greaterThanZero(true)
                                .build()),

                // getGreaterThan0 check should fail
                Arguments.of(
                        CreateQuestionResponseDto.builder()
                                .questionId(questionId)
                                .submissionId(submissionId)
                                .response("non Numeric Value")
                                .build()
                        , "Answer must only include numeric values/ decimal",
                        SubmissionQuestionValidation.builder()
                                .greaterThanZero(true)
                                .build())

        );


    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void validate_returnsFalseWhenInvalidResponseProvided(CreateQuestionResponseDto response, String message, SubmissionQuestionValidation validation) {

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId("1")
                .responseType(SubmissionQuestionResponseType.ShortAnswer)
                .validation(validation)
                .build();

        when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
        when(builder.addPropertyNode(Mockito.anyString()))
                .thenReturn(nodeBuilder);

        doReturn(question)
                .when(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);

        boolean isValid = validatorUnderTest.isValid(response, validatorContext);

        verify(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);
        verify(validatorContext).buildConstraintViolationWithTemplate(message);
        assertThat(isValid).isFalse();

    }

    private static Stream<Arguments> provideMandatoryQuestionTypes() {
        return Stream.of(
                Arguments.of(SubmissionQuestionResponseType.YesNo, "Select an option"),
                Arguments.of(SubmissionQuestionResponseType.MultipleSelection, "Select an option"),
                Arguments.of(SubmissionQuestionResponseType.SingleSelection, "Select an option"),
                Arguments.of(SubmissionQuestionResponseType.Dropdown, "Select at least one option"),
                Arguments.of(SubmissionQuestionResponseType.ShortAnswer, "You must enter an answer"),
                Arguments.of(SubmissionQuestionResponseType.LongAnswer, "You must enter an answer"),
                Arguments.of(SubmissionQuestionResponseType.Numeric, "You must enter an answer")

        );
    }

    @ParameterizedTest
    @MethodSource("provideMandatoryQuestionTypes")
    void validate_returnsCorrectMessageForMandatoryQuestionTypes(SubmissionQuestionResponseType responseType, String message) {

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(Boolean.TRUE)
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId("1")
                .responseType(responseType)
                .validation(validation)
                .build();

        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .questionId(questionId)
                .submissionId(submissionId)
                .build();

        when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
        when(builder.addPropertyNode(Mockito.anyString()))
                .thenReturn(nodeBuilder);

        doReturn(question)
                .when(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);

        validatorUnderTest.isValid(response, validatorContext);

        verify(validatorContext).buildConstraintViolationWithTemplate(message);
    }


    @Test
    void validate_returnsTrueIfValidResponseProvided() {

        final SubmissionQuestionValidation shortAnswerValidation = SubmissionQuestionValidation.builder()
                .mandatory(Boolean.TRUE)
                .minLength(2)
                .maxLength(255)
                .build();

        final SubmissionQuestion shortAnswerQuestion = SubmissionQuestion.builder()
                .questionId("1")
                .responseType(SubmissionQuestionResponseType.ShortAnswer)
                .validation(shortAnswerValidation)
                .build();

        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .submissionId(submissionId)
                .questionId(questionId)
                .response("a valid question response")
                .build();

        doReturn(shortAnswerQuestion)
                .when(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);

        boolean isValid = validatorUnderTest.isValid(response, validatorContext);

        verify(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);
        assertThat(isValid).isTrue();
    }

    @Test
    void throwsIllegalArgumentException_IfQuestionIdIsNull() {
        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .submissionId(submissionId)
                .response("a valid question response")
                .build();

        assertThrows(IllegalArgumentException.class, () -> validatorUnderTest.isValid(response, validatorContext));
    }

    @Test
    void throwsIllegalArgumentException_IfSectionIdIsNull() {

        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .questionId(questionId)
                .response("a valid question response")
                .build();

        assertThrows(IllegalArgumentException.class, () -> validatorUnderTest.isValid(response, validatorContext));
    }

    @Test
    void ThrowsNotFoundException_IfQuestionNotInDatabase() {

        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .submissionId(submissionId)
                .questionId(questionId)
                .response("a valid question response")
                .build();

        when(submissionService.getQuestionByQuestionId(USER_ID, submissionId, questionId))
                .thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> validatorUnderTest.isValid(response, validatorContext));
    }

    @Test
    void validate_ReturnsTrueIfDateIsValid() {

        final String dateDay = "02";
        final String dateMonth = "02";
        final String dateYear = "2022";
        final String[] dateParts = new String[]{dateDay, dateMonth, dateYear};

        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .questionId(questionId)
                .submissionId(submissionId)
                .multiResponse(dateParts)
                .build();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(Boolean.TRUE)
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId("1")
                .responseType(SubmissionQuestionResponseType.Date)
                .validation(validation)
                .build();

        doReturn(question)
                .when(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);

        boolean isValid = validatorUnderTest.isValid(response, validatorContext);

        assertThat(isValid).isTrue();
    }

    private static Stream<Arguments> provideInvalidDatesAndExpectedErrorMessages() {
        return Stream.of(
                Arguments.of(new String[]{}, "You must enter a date"), // mandatory check fails
                Arguments.of(new String[]{""}, "You must enter a date"), // if all elements in the array consist of empty strings then we'll count the entire response as being empty
                Arguments.of(new String[]{"", ""}, "You must enter a date"),
                Arguments.of(new String[]{"", "", ""}, "You must enter a date"),
                Arguments.of(new String[]{"", "02", "2022"}, "Date must include a day"),
                Arguments.of(new String[]{"", "", "2022"}, "Date must include a day and a month"),
                Arguments.of(new String[]{"", "02", ""}, "Date must include a day and a year"),
                Arguments.of(new String[]{"01", "", ""}, "Date must include a month and a year"),
                Arguments.of(new String[]{"02", "", "2022"}, "Date must include a month"),
                Arguments.of(new String[]{"02", "02", ""}, "Date must include a year"),
                Arguments.of(new String[]{"INVALID", "02", "2022"}, "Date must include a real day"), // day needs to be an integer value
                Arguments.of(new String[]{"02", "INVALID", "2022"}, "Date must include a real month"), // month needs to be an integer value
                Arguments.of(new String[]{"02", "02", "INVALID"}, "Date must include a real year"), // by the time year isn't an integer the sun will have exploded so think this test is safe
                Arguments.of(new String[]{"31", "04", "2022"}, "Date must include a real day"), // too many days in the month
                Arguments.of(new String[]{"31", "40", "2022"}, "Date must include a real month"), // too many months in the year
                Arguments.of(new String[]{"29", "02", "2022"}, "Date must include a real day"), // 2022 was not a leap year
                Arguments.of(new String[]{"29", "02", "-1"}, "Date must include a real year"), // -1 is less than 0000
                Arguments.of(new String[]{"29", "02", "10000"}, "Date must include a real year") // -1 is less than 0000
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDatesAndExpectedErrorMessages")
    void validate_returnsFalse_WithExpectedError(String[] dateParts, String message) {

        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .questionId(questionId)
                .submissionId(submissionId)
                .multiResponse(dateParts)
                .build();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(Boolean.TRUE)
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId("1")
                .responseType(SubmissionQuestionResponseType.Date)
                .validation(validation)
                .build();

        doReturn(question)
                .when(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);

        when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
        when(builder.addPropertyNode(Mockito.anyString()))
                .thenReturn(nodeBuilder);

        boolean isValid = validatorUnderTest.isValid(response, validatorContext);

        assertThat(isValid).isFalse();
        verify(validatorContext).buildConstraintViolationWithTemplate(message);
    }

    @Test
    void validate_ReturnsTrueIfAddressIsValid() {
        final String[] addressResponse = new String[]{"9 George Square", "", "Glasgow", "", "G2 1QQ"};

        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .questionId(questionId)
                .submissionId(submissionId)
                .multiResponse(addressResponse)
                .build();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(Boolean.TRUE)
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId("1")
                .responseType(SubmissionQuestionResponseType.AddressInput)
                .validation(validation)
                .build();

        doReturn(question)
                .when(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);

        boolean isValid = validatorUnderTest.isValid(response, validatorContext);

        assertThat(isValid).isTrue();
    }

    private static Stream<Arguments> provideAddressResponsesWhereArrayLengthIsNotFive() {
        return Stream.of(
                Arguments.of((Object) new String[]{"9 George Square"}),
                Arguments.of((Object) new String[]{"9 George Square", ""}),
                Arguments.of((Object) new String[]{"9 George Square", "", "Glasgow"}),
                Arguments.of((Object) new String[]{"9 George Square", "", "Glasgow", "", "G2 1QQ", "Some unnecessary additional info"})
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @MethodSource("provideAddressResponsesWhereArrayLengthIsNotFive")
    void validate_ThrowsIllegalArgumentException_IfAddressArraySizeIsNot5(String[] addressResponse) {

        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .questionId(questionId)
                .submissionId(submissionId)
                .multiResponse(addressResponse)
                .build();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(Boolean.TRUE)
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId("1")
                .responseType(SubmissionQuestionResponseType.AddressInput)
                .validation(validation)
                .build();

        doReturn(question)
                .when(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);

        assertThrows(IllegalArgumentException.class, () -> validatorUnderTest.isValid(response, validatorContext));
    }

    private static Stream<Arguments> provideInvalidAddressResponses() {
        return Stream.of(
                // tests the mandatory field validation
                Arguments.of(new String[]{"", "", "", "", ""}, List.of("You must enter an answer for address line 1", "You must enter an answer for town or city", "You must enter an answer for postcode")),

                // Tests min character length validation
                Arguments.of(new String[]{"9 George Square", "", "G", "", "G2 1QQ"}, List.of("Town or city must be 2 characters or more")),

                // Tests postcode min character length validation
                Arguments.of(new String[]{"9 George Square", "", "Glasgow", "", "GL22 1QQQ"}, List.of("Postcode must be 8 characters or less")),

                // Tests max character length validation
                Arguments.of(new String[]{"an address line that consists of greater than the max number of characters", "", "Glasgow", "", "G2 1QQ"}, List.of("Address line 1 must be 50 characters or less"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAddressResponses")
    void validate_ReturnsFalse_IfAddressIsInvalid(String[] addressResponse, List<String> messages) {
        final CreateQuestionResponseDto response = CreateQuestionResponseDto.builder()
                .questionId(questionId)
                .submissionId(submissionId)
                .multiResponse(addressResponse)
                .build();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(Boolean.TRUE)
                .minLength(2)
                .maxLength(50)
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId("1")
                .responseType(SubmissionQuestionResponseType.AddressInput)
                .validation(validation)
                .build();

        doReturn(question)
                .when(submissionService).getQuestionByQuestionId(USER_ID, submissionId, questionId);

        when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
        when(builder.addPropertyNode(Mockito.anyString()))
                .thenReturn(nodeBuilder);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        boolean isValid = validatorUnderTest.isValid(response, validatorContext);

        assertThat(isValid).isFalse();
        verify(validatorContext, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());

        List<String> capturedMessages = messageCaptor.getAllValues();

        final boolean listsContainTheSameMessages = capturedMessages.containsAll(messages) && messages.containsAll(capturedMessages);
        assertThat(listsContainTheSameMessages).isTrue();
    }
}