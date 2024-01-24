package gov.cabinetoffice.gap.applybackend.testData;


import gov.cabinetoffice.gap.applybackend.enums.SubmissionQuestionResponseType;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.model.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestData {
    public static final SubmissionQuestion ELIGIBILITY_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("ELIGIBILITY")
            .fieldTitle("Eligibility Statement")
            .displayText("Some admin supplied text describing what it means to be eligible to apply for this grant")
            .questionSuffix("Does your organisation meet the eligibility criteria?")
            .responseType(SubmissionQuestionResponseType.YesNo)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .response("Yes")
            .build();

    public static final SubmissionQuestion V1_ORG_TYPE_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_TYPE").fieldTitle("Choose your organisation type")
            .profileField("ORG_TYPE")
            .hintText("Choose the option that best describes your organisation")
            .responseType(SubmissionQuestionResponseType.Dropdown)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .options(new String[]{
                    "Limited company",
                    "Non-limited company",
                    "Registered charity",
                    "Unregistered charity",
                    "Other"
            })
            .response("V1_Limited company")
            .build();

    public static final SubmissionQuestion V1_ORG_NAME_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_NAME")
            .profileField("ORG_NAME")
            .fieldTitle("Enter the name of your organisation")
            .hintText(
                    "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .minLength(5)
                            .maxLength(100)
                            .build()
            )
            .response("V1_Company name")
            .build();

    public static final SubmissionQuestion V1_ORG_AMOUNT_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_AMOUNT")
            .profileField("ORG_AMOUNT")
            .fieldTitle("Enter the money you would wish to receive")
            .hintText(
                    "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .minLength(5)
                            .maxLength(100)
                            .build()
            )
            .response("500")
            .build();

    public static final SubmissionQuestion V1_ORG_ADDRESS_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_ADDRESS")
            .profileField("ORG_ADDRESS")
            .fieldTitle("Enter your organisation's address")
            .responseType(SubmissionQuestionResponseType.AddressInput)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .multiResponse(new String[]{
                    "V1_test address",
                    "",
                    "V1_Edinburgh",
                    "",
                    "V1_TEST_POSTCODE"
            })
            .build();

    public static final SubmissionQuestion V1_ORG_COMPANIES_NO_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_COMPANIES_HOUSE")
            .profileField("ORG_COMPANIES_HOUSE")
            .fieldTitle("Please supply the Companies House number for your organisation - if applicable")
            .hintText(
                    "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .response("V1_CHN")
            .build();

    public static final SubmissionQuestion V1_ORG_CHARITY_NO_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_CHARITY_NUMBER")
            .profileField("ORG_CHARITY_COMMISSION_NUMBER")
            .fieldTitle("Please supply the Charity Commission number for your organisation - if applicable")
            .hintText(
                    "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .response("V1_CMSN_NO")
            .build();

    public static final SubmissionQuestion V1_ORG_BENEFICIARY_LOC_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("BENEFITIARY_LOCATION")
            .fieldTitle("Where will this funding be spent?")
            .hintText(
                    "Select the location where the grant funding will be spent. You can choose more than one, if it is being spent in more than one location.\\n\\nSelect all that apply:")
            .adminSummary("where the funding will be spent")
            .responseType(SubmissionQuestionResponseType.MultipleSelection)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .options(new String[]{
                    "North East England",
                    "North West England",
                    "South East England",
                    "South West England",
                    "Midlands", "Scotland",
                    "Wales",
                    "Northern Ireland"
            })
            .multiResponse(new String[]{
                    "V1_Scotland",
                    "V1_North East England"
            })
            .build();

    public static final SubmissionQuestion V2_ORG_TYPE_SUBMISSION_QUESTION_LIMITED = SubmissionQuestion.builder()
            .questionId("APPLICANT_TYPE").fieldTitle("Choose your organisation type").
            profileField("ORG_TYPE")
            .hintText("Choose the option that best describes your organisation")
            .responseType(SubmissionQuestionResponseType.Dropdown)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            ).options(new String[]{
                    "Limited company",
                    "Non-limited company",
                    "Registered charity",
                    "Unregistered charity",
                    "Other",
                    "I am applying as an individual"
            })
            .response("V2_Limited company")
            .build();

    public static final SubmissionQuestion V2_ORG_TYPE_SUBMISSION_QUESTION_NON_LIMITED = SubmissionQuestion.builder()
            .questionId("APPLICANT_TYPE").fieldTitle("Choose your organisation type").
            profileField("ORG_TYPE")
            .hintText("Choose the option that best describes your organisation")
            .responseType(SubmissionQuestionResponseType.Dropdown)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            ).options(new String[]{
                    "Limited company",
                    "Non-limited company",
                    "Registered charity",
                    "Unregistered charity",
                    "Other",
                    "I am applying as an individual"
            })
            .response("Non-limited company")
            .build();

    public static final SubmissionQuestion V2_ORG_TYPE_SUBMISSION_QUESTION_INDIVIDUAL = SubmissionQuestion.builder()
            .questionId("APPLICANT_TYPE").fieldTitle("Choose your organisation type").
            profileField("ORG_TYPE")
            .hintText("Choose the option that best describes your organisation")
            .responseType(SubmissionQuestionResponseType.Dropdown)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            ).options(new String[]{
                    "Limited company",
                    "Non-limited company",
                    "Registered charity",
                    "Unregistered charity",
                    "Other",
                    "I am applying as an individual"
            })
            .response("I am applying as an individual")
            .build();

    public static final SubmissionQuestion V2_ORG_NAME_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_NAME")
            .profileField("ORG_NAME")
            .fieldTitle("Enter the name of your organisation")
            .hintText(
                    "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .minLength(5)
                            .maxLength(100)
                            .build()
            )
            .response("V2_Company name")
            .build();

    public static final SubmissionQuestion V2_ORG_AMOUNT_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_AMOUNT")
            .profileField("ORG_AMOUNT")
            .fieldTitle("Enter the money you would wish to receive")
            .hintText(
                    "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .minLength(5)
                            .maxLength(100)
                            .build()
            )
            .response("500")
            .build();

    public static final SubmissionQuestion V2_ORG_ADDRESS_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_ADDRESS")
            .profileField("ORG_ADDRESS")
            .fieldTitle("Enter your organisation's address")
            .responseType(SubmissionQuestionResponseType.AddressInput)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .multiResponse(new String[]{
                    "V2_test address",
                    "",
                    "V2_Edinburgh",
                    "",
                    "V2_POSTCODE"
            })
            .build();

    public static final SubmissionQuestion V2_ORG_COMPANIES_NO_SUBMISSION_QUESTION_WITH_RESPONSE = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_COMPANIES_HOUSE")
            .profileField("ORG_COMPANIES_HOUSE")
            .fieldTitle("Please supply the Companies House number for your organisation - if applicable")
            .hintText(
                    "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .response("V2_CHN")
            .build();

    public static final SubmissionQuestion V2_ORG_COMPANIES_NO_SUBMISSION_QUESTION_WITHOUT_RESPONSE = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_COMPANIES_HOUSE")
            .profileField("ORG_COMPANIES_HOUSE")
            .fieldTitle("Please supply the Companies House number for your organisation - if applicable")
            .hintText(
                    "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .response("")
            .build();

    public static final SubmissionQuestion V2_ORG_CHARITY_NO_SUBMISSION_QUESTION_WITH_RESPONSE = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_CHARITY_NUMBER")
            .profileField("ORG_CHARITY_COMMISSION_NUMBER")
            .fieldTitle("Please supply the Charity Commission number for your organisation - if applicable")
            .hintText(
                    "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .response("V2_CMSN_NO")
            .build();

    public static final SubmissionQuestion V2_ORG_CHARITY_NO_SUBMISSION_QUESTION_WITHOUT_RESPONSE = SubmissionQuestion.builder()
            .questionId("APPLICANT_ORG_CHARITY_NUMBER")
            .profileField("ORG_CHARITY_COMMISSION_NUMBER")
            .fieldTitle("Please supply the Charity Commission number for your organisation - if applicable")
            .hintText(
                    "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation
                            .builder()
                            .mandatory(true)
                            .build()
            )
            .response("")
            .build();

    public static final SubmissionQuestion V2_ORG_BENEFICIARY_LOC_SUBMISSION_QUESTION = SubmissionQuestion.builder()
            .questionId("BENEFITIARY_LOCATION")
            .fieldTitle("Where will this funding be spent?")
            .hintText(
                    "Select the location where the grant funding will be spent. You can choose more than one, if it is being spent in more than one location.\\n\\nSelect all that apply:")
            .adminSummary("where the funding will be spent")
            .responseType(SubmissionQuestionResponseType.MultipleSelection)
            .validation(SubmissionQuestionValidation.builder().mandatory(true).build())
            .options(new String[]{
                    "North East England",
                    "North West England",
                    "South East England",
                    "South West England",
                    "Midlands",
                    "Scotland",
                    "Wales",
                    "Northern Ireland"
            })
            .multiResponse(new String[]{"V2_Scotland", "V2_North East England"})
            .build();


    public static final SubmissionSection ELIGIBILITY_SECTION_SUBMISSION = SubmissionSection
            .builder()
            .sectionId("ELIGIBILITY")
            .sectionTitle("Eligibility")
            .sectionStatus(SubmissionSectionStatus.COMPLETED)
            .questions(Collections.singletonList(ELIGIBILITY_SUBMISSION_QUESTION))
            .build();

    public static final SubmissionSection ESSENTIAL_SECTION_SUBMISSION = SubmissionSection.builder()
            .sectionId("ESSENTIAL").sectionTitle("Essential Information")
            .sectionStatus(SubmissionSectionStatus.COMPLETED)
            .questions(Arrays.asList(
                    V1_ORG_TYPE_SUBMISSION_QUESTION,
                    V1_ORG_NAME_SUBMISSION_QUESTION,
                    V1_ORG_AMOUNT_SUBMISSION_QUESTION,
                    V1_ORG_ADDRESS_SUBMISSION_QUESTION,
                    V1_ORG_COMPANIES_NO_SUBMISSION_QUESTION,
                    V1_ORG_CHARITY_NO_SUBMISSION_QUESTION,
                    V1_ORG_BENEFICIARY_LOC_SUBMISSION_QUESTION
            ))
            .build();

    public static final SubmissionSection ORGANISATION_DETAILS_SECTION_SUBMISSION_LIMITED_WITH_CC_AND_CH = SubmissionSection.builder()
            .sectionId("ORGANISATION_DETAILS")
            .sectionTitle("Your organisation")
            .sectionStatus(SubmissionSectionStatus.COMPLETED)
            .questions(Arrays.asList(
                    V2_ORG_TYPE_SUBMISSION_QUESTION_LIMITED,
                    V2_ORG_NAME_SUBMISSION_QUESTION,
                    V2_ORG_ADDRESS_SUBMISSION_QUESTION,
                    V2_ORG_COMPANIES_NO_SUBMISSION_QUESTION_WITH_RESPONSE,
                    V2_ORG_CHARITY_NO_SUBMISSION_QUESTION_WITH_RESPONSE
            ))
            .build();

    public static final SubmissionSection ORGANISATION_DETAILS_SECTION_SUBMISSION_LIMITED_WITHOUT_CC_AND_CH = SubmissionSection.builder()
            .sectionId("ORGANISATION_DETAILS")
            .sectionTitle("Your organisation")
            .sectionStatus(SubmissionSectionStatus.COMPLETED)
            .questions(Arrays.asList(
                    V2_ORG_TYPE_SUBMISSION_QUESTION_LIMITED,
                    V2_ORG_NAME_SUBMISSION_QUESTION,
                    V2_ORG_ADDRESS_SUBMISSION_QUESTION,
                    V2_ORG_COMPANIES_NO_SUBMISSION_QUESTION_WITHOUT_RESPONSE,
                    V2_ORG_CHARITY_NO_SUBMISSION_QUESTION_WITHOUT_RESPONSE
            ))
            .build();

    public static final SubmissionSection ORGANISATION_DETAILS_SECTION_SUBMISSION_NON_LIMITED = SubmissionSection.builder()
            .sectionId("ORGANISATION_DETAILS")
            .sectionTitle("Your organisation")
            .sectionStatus(SubmissionSectionStatus.COMPLETED)
            .questions(Arrays.asList(
                    V2_ORG_TYPE_SUBMISSION_QUESTION_NON_LIMITED,
                    V2_ORG_NAME_SUBMISSION_QUESTION,
                    V2_ORG_ADDRESS_SUBMISSION_QUESTION
            ))
            .build();


    public static final SubmissionSection ORGANISATION_DETAILS_SECTION_SUBMISSION_INDIVIDUAL = SubmissionSection.builder()
            .sectionId("ORGANISATION_DETAILS")
            .sectionTitle("Your details")
            .sectionStatus(SubmissionSectionStatus.COMPLETED)
            .questions(Arrays.asList(
                    V2_ORG_TYPE_SUBMISSION_QUESTION_INDIVIDUAL,
                    V2_ORG_NAME_SUBMISSION_QUESTION,
                    V2_ORG_ADDRESS_SUBMISSION_QUESTION
            ))
            .build();

    public static final SubmissionSection FUNDING_DETAILS_SECTION_SUBMISSION = SubmissionSection
            .builder()
            .sectionId("FUNDING_DETAILS")
            .sectionTitle("Funding")
            .sectionStatus(SubmissionSectionStatus.COMPLETED)
            .questions(Arrays.asList(V2_ORG_AMOUNT_SUBMISSION_QUESTION, V2_ORG_BENEFICIARY_LOC_SUBMISSION_QUESTION))
            .build();

    public static final List<SubmissionSection> V2_SUBMISSION_SECTIONS_LIST_LIMITED_WITH_CC_AND_CH = Arrays.asList(
            ELIGIBILITY_SECTION_SUBMISSION,
            ORGANISATION_DETAILS_SECTION_SUBMISSION_LIMITED_WITH_CC_AND_CH,
            FUNDING_DETAILS_SECTION_SUBMISSION
    );

    public static final Submission V2_SUBMISSION_LIMITED_COMPANY_WITH_CC_AND_CH = Submission
            .builder()
            .scheme(GrantScheme.builder().version(2).name("v2 limited Scheme Name").id(1).build())
            .gapId("GAP-1")
            .submittedDate(ZonedDateTime.parse("2022-02-15T18:35:24.00Z"))
            .definition(SubmissionDefinition.builder().sections((V2_SUBMISSION_SECTIONS_LIST_LIMITED_WITH_CC_AND_CH)).build())
            .version(2)
            .build();

    public static final List<SubmissionSection> V2_SUBMISSION_SECTIONS_LIST_LIMITED_WITHOUT_CC_AND_CH = Arrays.asList(
            ELIGIBILITY_SECTION_SUBMISSION,
            ORGANISATION_DETAILS_SECTION_SUBMISSION_LIMITED_WITHOUT_CC_AND_CH,
            FUNDING_DETAILS_SECTION_SUBMISSION
    );

    public static final Submission V2_SUBMISSION_LIMITED_COMPANY_WITHOUT_CC_AND_CH = Submission
            .builder()
            .scheme(GrantScheme.builder().version(2).name("Test Org Name v2").build())
            .gapId("GAP-2")
            .submittedDate(ZonedDateTime.parse("2022-02-15T18:35:24.00Z"))
            .definition(SubmissionDefinition.builder().sections((V2_SUBMISSION_SECTIONS_LIST_LIMITED_WITHOUT_CC_AND_CH)).build())
            .build();

    public static final List<SubmissionSection> V2_SUBMISSION_SECTIONS_LIST_NON_LIMITED = Arrays.asList(
            ELIGIBILITY_SECTION_SUBMISSION,
            ORGANISATION_DETAILS_SECTION_SUBMISSION_NON_LIMITED,
            FUNDING_DETAILS_SECTION_SUBMISSION
    );

    public static final Submission V2_SUBMISSION_NON_LIMITED_COMPANY = Submission
            .builder()
            .scheme(GrantScheme.builder().version(2).name("Test Org Name v2").build())
            .gapId("GAP-2")
            .submittedDate(ZonedDateTime.parse("2022-02-15T18:35:24.00Z"))
            .definition(SubmissionDefinition.builder().sections((V2_SUBMISSION_SECTIONS_LIST_NON_LIMITED)).build())
            .version(2)
            .build();

    public static final List<SubmissionSection> V2_SUBMISSION_SECTIONS_LIST_INDIVIDUAL = Arrays.asList(
            ELIGIBILITY_SECTION_SUBMISSION,
            ORGANISATION_DETAILS_SECTION_SUBMISSION_INDIVIDUAL,
            FUNDING_DETAILS_SECTION_SUBMISSION
    );

    public static final Submission V2_SUBMISSION_INDIVIDUAL = Submission
            .builder()
            .scheme(GrantScheme.builder().version(2).name("Test Org Name v2").build())
            .gapId("GAP-2")
            .submittedDate(ZonedDateTime.parse("2022-02-15T18:35:24.00Z"))
            .definition(SubmissionDefinition.builder().sections((V2_SUBMISSION_SECTIONS_LIST_INDIVIDUAL)).build())
            .version(2)
            .build();
    public static final SubmissionSection V1_CUSTOM_SECTION_SUBMISSION = SubmissionSection
            .builder()
            .sectionId("26215de2-2211-48e4-9c82-835cd227daad").sectionTitle("My Custom Section")
            .sectionStatus(SubmissionSectionStatus.COMPLETED)
            .questions(Arrays.asList(V1_ORG_TYPE_SUBMISSION_QUESTION, V1_ORG_NAME_SUBMISSION_QUESTION,
                    V1_ORG_AMOUNT_SUBMISSION_QUESTION, V1_ORG_ADDRESS_SUBMISSION_QUESTION,
                    V1_ORG_COMPANIES_NO_SUBMISSION_QUESTION, V1_ORG_CHARITY_NO_SUBMISSION_QUESTION,
                    V1_ORG_BENEFICIARY_LOC_SUBMISSION_QUESTION,
                    SubmissionQuestion.builder()
                            .fieldTitle("Upload an important file")
                            .responseType(SubmissionQuestionResponseType.SingleFileUpload)
                            .response("file_name.pdf").build()))
            .build();

    public static final List<SubmissionSection> V1_SUBMISSION_SECTIONS_LIST = Arrays.asList(
            ELIGIBILITY_SECTION_SUBMISSION,
            ESSENTIAL_SECTION_SUBMISSION,
            V1_CUSTOM_SECTION_SUBMISSION
    );

    public static final Submission V1_SUBMISSION = Submission
            .builder()
            .scheme(GrantScheme.builder().name("Test Org Name v2").build())
            .gapId("GAP-2")
            .submittedDate(ZonedDateTime.parse("2022-02-15T18:35:24.00Z"))
            .definition(SubmissionDefinition.builder().sections(V1_SUBMISSION_SECTIONS_LIST).build())
            .version(1)
            .build();

}

