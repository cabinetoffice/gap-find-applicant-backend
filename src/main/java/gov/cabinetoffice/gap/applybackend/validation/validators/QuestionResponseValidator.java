package gov.cabinetoffice.gap.applybackend.validation.validators;

import gov.cabinetoffice.gap.applybackend.constants.ValidationConstants;
import gov.cabinetoffice.gap.applybackend.dto.api.CreateQuestionResponseDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionQuestionResponseType;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestion;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestionValidation;
import gov.cabinetoffice.gap.applybackend.service.SubmissionService;
import gov.cabinetoffice.gap.applybackend.validation.ValidationResult;
import gov.cabinetoffice.gap.applybackend.validation.annotations.ValidQuestionResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Month;
import java.time.Year;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static gov.cabinetoffice.gap.applybackend.utils.SecurityContextHelper.getUserIdFromSecurityContext;


@RequiredArgsConstructor
public class QuestionResponseValidator implements ConstraintValidator<ValidQuestionResponse, Object> {


    private final SubmissionService submissionService;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        constraintValidatorContext.disableDefaultConstraintViolation();
        CreateQuestionResponseDto submittedQuestion = (CreateQuestionResponseDto) value;

        SubmissionQuestion questionFromDb = getQuestionFromDatabase(submittedQuestion);

        ValidationResult result = validate(submittedQuestion, questionFromDb);

        if (!result.isValid()) {
            for (Map.Entry<String, String> entry : result.getFieldErrors().entrySet()) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(entry.getValue())
                        .addPropertyNode(entry.getKey())
                        .addConstraintViolation();
            }
        }

        return result.isValid();
    }

    private SubmissionQuestion getQuestionFromDatabase(CreateQuestionResponseDto submittedQuestion) {

        if (submittedQuestion.getSubmissionId() == null) {
            throw new IllegalArgumentException("Submission ID must not be null.");
        }

        if (StringUtils.isEmpty(submittedQuestion.getQuestionId())) {
            throw new IllegalArgumentException("Question ID must not be null.");
        }

        final String applicantId = getUserIdFromSecurityContext();
        return submissionService.getQuestionByQuestionId(applicantId, submittedQuestion.getSubmissionId(), submittedQuestion.getQuestionId());
    }

    private ValidationResult validate(CreateQuestionResponseDto submittedQuestion, SubmissionQuestion question) {

        SubmissionQuestionValidation validation = question.getValidation();
        ValidationResult result = ValidationResult.builder().build();

        // Dates need to be handled differently
        if (question.getResponseType().equals(SubmissionQuestionResponseType.Date)) {
            return validateDate(submittedQuestion.getMultiResponse(), question.getValidation().isMandatory());
        }

        // Addresses need to be handled differently
        if (question.getResponseType().equals(SubmissionQuestionResponseType.AddressInput)) {
            if (submittedQuestion.getMultiResponse() == null || submittedQuestion.getMultiResponse().length != 5) {
                throw new IllegalArgumentException("Arrays containing address responses must have exactly 5 elements, representing Address1, Address2, Town, County, Postcode");
            }
            return validateAddress(submittedQuestion, question.getValidation());
        }

        final boolean singleResponseIsEmpty = StringUtils.isEmpty(submittedQuestion.getResponse());
        final boolean multiResponseIsEmpty = arrayAnswerIsEmpty(submittedQuestion.getMultiResponse());

        if (validation.isMandatory()) {
            // if question is mandatory and responses are empty, fail validation
            if (singleResponseIsEmpty && multiResponseIsEmpty) {
                final String message = getMandatoryFieldViolationMessage(question);
                result.addError(ValidationConstants.SINGLE_RESPONSE_FIELD, message);
                return result;
            }
        } else {
            // if question is optional and responses are empty, pass validation
            if (singleResponseIsEmpty && multiResponseIsEmpty) {
                result.setValid(Boolean.TRUE);
                return result;
            }
        }

        if (validation.getMinLength() != null &&
                submittedQuestion.getResponse().length() < validation.getMinLength()) {
            result.addError(ValidationConstants.SINGLE_RESPONSE_FIELD, String.format("Answer must be %s characters or more", validation.getMinLength()));
            return result;
        }

        if (validation.getMaxLength() != null &&
                submittedQuestion.getResponse().length() > validation.getMaxLength()) {
            result.addError(ValidationConstants.SINGLE_RESPONSE_FIELD, String.format("Answer must be %s characters or less", validation.getMaxLength()));
            return result;
        }

        if (validation.getMinWords() != null &&
                getNumberOfWords(submittedQuestion.getResponse()) < validation.getMinWords()) {
            result.addError(ValidationConstants.SINGLE_RESPONSE_FIELD, String.format("Answer must be %s words or more", validation.getMinWords()));
            return result;
        }

        if (validation.getMaxWords() != null &&
                getNumberOfWords(submittedQuestion.getResponse()) > validation.getMaxWords()) {
            result.addError(ValidationConstants.SINGLE_RESPONSE_FIELD, String.format("Answer must be %s words or less", validation.getMaxWords()));
            return result;
        }

        if (validation.getGreaterThanZero() != null &&
                validation.getGreaterThanZero()) {
            try {
                if (Float.parseFloat(submittedQuestion.getResponse()) <= 0F) {
                    result.addError(ValidationConstants.SINGLE_RESPONSE_FIELD, "Answer must have a value greater than zero");
                    return result;
                }
            } catch (final NumberFormatException e) {
                result.addError(ValidationConstants.SINGLE_RESPONSE_FIELD, "Answer must only include numeric values/ decimal");
                return result;
            }
        }

        result.setValid(Boolean.TRUE);
        return result;
    }

    private ValidationResult validateAddress(CreateQuestionResponseDto response, SubmissionQuestionValidation validation) {

        final String[] addressResponse = response.getMultiResponse();
        ValidationResult addressValidationResult = ValidationResult.builder().build();

        if (validation.isMandatory()) {
            validateMandatoryAddressFields(addressResponse, addressValidationResult);
        }

        if (!arrayAnswerIsEmpty(addressResponse)) {
            final int textFieldMaxLength = validation.getMaxLength() != null ? validation.getMaxLength() : ValidationConstants.SHORT_TEXT_MAX_LENGTH_DEFAULT;
            final int textFieldMinLength = validation.getMinLength() != null ? validation.getMinLength() : ValidationConstants.SHORT_TEXT_MIN_LENGTH_DEFAULT;

            validateAddressField(addressResponse[0], "multiResponse[0]", "Address line 1", textFieldMinLength, textFieldMaxLength, addressValidationResult);
            validateAddressField(addressResponse[1], "multiResponse[1]", "Address line 2", textFieldMinLength, textFieldMaxLength, addressValidationResult);
            validateAddressField(addressResponse[2], "multiResponse[2]", "Town or city", textFieldMinLength, textFieldMaxLength, addressValidationResult);
            validateAddressField(addressResponse[3], "multiResponse[3]", "County", textFieldMinLength, textFieldMaxLength, addressValidationResult);
            validateAddressField(addressResponse[4], "multiResponse[4]", "Postcode", textFieldMinLength, ValidationConstants.POSTCODE_MAX_LENGTH, addressValidationResult);
        }

        if (addressValidationResult.getFieldErrors().isEmpty()) {
            addressValidationResult.setValid(Boolean.TRUE);
        }

        return addressValidationResult;
    }

    private void validateMandatoryAddressFields(String[] addressResponse, ValidationResult addressValidationResult) {
        if (addressResponse[0].isBlank()) {
            addressValidationResult.addError("multiResponse[0]", "You must enter an answer for address line 1");
        }

        if (addressResponse[2].isBlank()) {
            addressValidationResult.addError("multiResponse[2]", "You must enter an answer for town or city");
        }

        if (addressResponse[4].isBlank()) {
            addressValidationResult.addError("multiResponse[4]", "You must enter an answer for postcode");
        }
    }

    private void validateAddressField(String fieldValue, String fieldName, String fieldDisplayName, int minLength, int maxLength, ValidationResult validationResult) {
        if (!fieldValue.isBlank() && fieldValue.length() < minLength) {
            validationResult.addError(fieldName, String.format("%s must be %s characters or more", fieldDisplayName, minLength));
        }

        if (fieldValue.length() > maxLength) {
            validationResult.addError(fieldName, String.format("%s must be %s characters or less", fieldDisplayName, maxLength));
        }
    }

    private String getMandatoryFieldViolationMessage(SubmissionQuestion question) {
        switch (question.getResponseType()) {
            case Dropdown:
                return "Select at least one option";
            case YesNo, MultipleSelection, SingleSelection:
                return "Select an option";
            case ShortAnswer, LongAnswer, AddressInput, Numeric:
            default:
                return "You must enter an answer";
        }
    }

    private int getNumberOfWords(String response) {
        return Strings.isEmpty(response) ? 0 : response.split("\\s").length;
    }

    private ValidationResult validateDate(final String[] dateComponents, final boolean isMandatory) {

        ValidationResult dateValidationResult = ValidationResult.builder().build();

        if (isMandatory && arrayAnswerIsEmpty(dateComponents)) {
            dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[0]", "You must enter a date");
            return dateValidationResult;
        }

        if (!arrayAnswerIsEmpty(dateComponents)) {

            boolean dayFails = false, monthFails = false, yearFails = false;

            if (Strings.isEmpty(dateComponents[0])) {
                dayFails = true;
            }

            if (Strings.isEmpty(dateComponents[1])) {
                monthFails = true;
            }

            if (Strings.isEmpty(dateComponents[2])) {
                yearFails = true;
            }

            if (dayFails && monthFails) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[0]", "Date must include a day and a month");
                return dateValidationResult;
            }

            if (dayFails && yearFails) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[0]", "Date must include a day and a year");
                return dateValidationResult;
            }

            if (dayFails) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[0]", "Date must include a day");
                return dateValidationResult;
            }

            if (monthFails && yearFails) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[1]", "Date must include a month and a year");
                return dateValidationResult;
            }

            if (monthFails) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[1]", "Date must include a month");
                return dateValidationResult;
            }

            if (yearFails) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[2]", "Date must include a year");
                return dateValidationResult;
            }

            int day;
            try {
                day = Integer.parseInt(dateComponents[0]);
            } catch (NumberFormatException e) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[0]", "Date must include a real day");
                return dateValidationResult;
            }

            int month;
            try {
                month = Integer.parseInt(dateComponents[1]);
            } catch (NumberFormatException e) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[1]", "Date must include a real month");
                return dateValidationResult;
            }

            int year;
            try {
                year = Integer.parseInt(dateComponents[2]);
            } catch (NumberFormatException e) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[2]", "Date must include a real year");
                return dateValidationResult;
            }

            if (month > 12) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[1]", "Date must include a real month");
                return dateValidationResult;
            }

            if (year < 0000 || year > 9999) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[2]", "Date must include a real year");
                return dateValidationResult;
            }

            if (!dayIsValidForMonth(day, month, year)) {
                dateValidationResult.addError(ValidationConstants.MULTI_RESPONSE_FIELD + "[0]", "Date must include a real day");
                return dateValidationResult;
            }
        }

        dateValidationResult.setValid(Boolean.TRUE);
        return dateValidationResult;
    }

    private boolean dayIsValidForMonth(final int day, final int month, final int year) {
        final Year parsedYear = Year.of(year);
        final Month parsedMonth = Month.of(month);
        final boolean isLeap = parsedYear.isLeap();

        return day <= parsedMonth.length(isLeap);
    }

    private boolean arrayAnswerIsEmpty(final String[] answerParts) {
        if (answerParts == null || answerParts.length == 0) {
            return Boolean.TRUE;
        } else {
            return Stream.of(answerParts)
                    .allMatch(String::isBlank);
        }
    }
}
