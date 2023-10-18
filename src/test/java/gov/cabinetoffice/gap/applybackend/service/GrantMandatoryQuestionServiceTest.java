package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantMandatoryQuestionServiceTest {

    final ArgumentCaptor<GrantMandatoryQuestions> captor = ArgumentCaptor.forClass(GrantMandatoryQuestions.class);
    private final String applicantUserId = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";
    private final UUID MANDATORY_QUESTION_ID = UUID.fromString("8e33d655-556e-49d5-bc46-3cfa4fdfa00f");
    @Mock
    private GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;
    @InjectMocks
    private GrantMandatoryQuestionService serviceUnderTest;

    @Test
    void getGrantMandatoryQuestionById_ThrowsNotFoundException() {
        final UUID mandatoryQuestionsId = UUID.randomUUID();

        when(grantMandatoryQuestionRepository.findById(mandatoryQuestionsId))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> serviceUnderTest.getGrantMandatoryQuestionById(mandatoryQuestionsId, applicantUserId));
    }

    @Test
    void getGrantMandatoryQuestionById_ThrowsForbiddenException() {

        final GrantApplicant applicant = GrantApplicant
                .builder()
                .userId(applicantUserId)
                .build();

        final Optional<GrantMandatoryQuestions> mandatoryQuestions = Optional.of(GrantMandatoryQuestions
                .builder()
                .createdBy(applicant)
                .build());

        final UUID mandatoryQuestionsId = UUID.randomUUID();
        final String invalidUserId = "a-bad-user-id";

        when(grantMandatoryQuestionRepository.findById(mandatoryQuestionsId))
                .thenReturn(mandatoryQuestions);

        assertThrows(ForbiddenException.class, () -> serviceUnderTest.getGrantMandatoryQuestionById(mandatoryQuestionsId, invalidUserId));
    }

    @Test
    void getGrantMandatoryQuestionById_ReturnsExpectedMandatoryQuestions() {

        final GrantApplicant applicant = GrantApplicant
                .builder()
                .userId(applicantUserId)
                .build();

        final Optional<GrantMandatoryQuestions> mandatoryQuestions = Optional.of(GrantMandatoryQuestions
                .builder()
                .createdBy(applicant)
                .build());

        final UUID mandatoryQuestionsId = UUID.randomUUID();

        when(grantMandatoryQuestionRepository.findById(mandatoryQuestionsId))
                .thenReturn(mandatoryQuestions);

        final GrantMandatoryQuestions methodResponse = serviceUnderTest.getGrantMandatoryQuestionById(mandatoryQuestionsId, applicantUserId);

        assertThat(methodResponse).isEqualTo(mandatoryQuestions.get());
    }

    @Test
    void createMandatoryQuestion_ReturnsExistingMandatoryQuestions_InsteadOfCreatingNewOnes() {

        final GrantMandatoryQuestions existingMandatoryQuestions = GrantMandatoryQuestions.
                builder()
                .build();

        final GrantScheme scheme = GrantScheme
                .builder()
                .id(1)
                .build();

        final GrantApplicant applicant = GrantApplicant
                .builder()
                .userId(applicantUserId)
                .build();

        when(grantMandatoryQuestionRepository.findByGrantSchemeAndCreatedBy(scheme, applicant))
                .thenReturn(List.of(existingMandatoryQuestions));

        final GrantMandatoryQuestions methodResponse = serviceUnderTest.createMandatoryQuestion(scheme, applicant);

        verify(grantMandatoryQuestionRepository, never()).save(Mockito.any());
        assertThat(methodResponse).isEqualTo(existingMandatoryQuestions);
    }


    @Test
    void createMandatoryQuestion_CreatesNewEntry_IfNoExistingQuestionsFound() {

        final GrantScheme scheme = GrantScheme
                .builder()
                .id(1)
                .build();

        final GrantApplicant applicant = GrantApplicant
                .builder()
                .userId(applicantUserId)
                .build();

        final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder()
                .grantScheme(scheme)
                .createdBy(applicant)
                .build();

        when(grantMandatoryQuestionRepository.findByGrantSchemeAndCreatedBy(scheme, applicant))
                .thenReturn(Collections.emptyList());

        when(grantMandatoryQuestionRepository.save(Mockito.any()))
                .thenReturn(grantMandatoryQuestions);

        final GrantMandatoryQuestions methodResponse = serviceUnderTest.createMandatoryQuestion(scheme, applicant);

        verify(grantMandatoryQuestionRepository).save(any());
        assertThat(methodResponse).isEqualTo(grantMandatoryQuestions);
    }

    @Test
    void updateMandatoryQuestion_ThrowsNotFoundException() {
        final UUID mandatoryQuestionsId = UUID.randomUUID();

        final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                .builder()
                .id(mandatoryQuestionsId)
                .build();

        when(grantMandatoryQuestionRepository.findById(mandatoryQuestionsId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serviceUnderTest.updateMandatoryQuestion(grantMandatoryQuestions));
    }

    @Test
    void updateMandatoryQuestion_UpdatesExpectedMandatoryQuestions() {
        final UUID mandatoryQuestionsId = UUID.randomUUID();

        final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                .builder()
                .id(mandatoryQuestionsId)
                .build();

        when(grantMandatoryQuestionRepository.findById(mandatoryQuestionsId))
                .thenReturn(Optional.of(grantMandatoryQuestions));

        when(grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                .thenReturn(grantMandatoryQuestions);

        final GrantMandatoryQuestions methodResponse = serviceUnderTest.updateMandatoryQuestion(grantMandatoryQuestions);

        verify(grantMandatoryQuestionRepository).save(grantMandatoryQuestions);
        assertThat(methodResponse).isEqualTo(grantMandatoryQuestions);
    }

    @Test
    public void testGenerateNextPageUrl_summaryPageConditionsMet() {
        final GrantMandatoryQuestions mandatoryQuestion = createMandatoryQuestion(true, true, true, true, true, true, true, true, true);
        final String nextPageUrl = serviceUnderTest.generateNextPageUrl(mandatoryQuestion);
        assertThat(nextPageUrl).isEqualTo("/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-summary");
    }

    @Test
    public void testGenerateNextPageUrl_fundingLocationConditionsMet() {
        final GrantMandatoryQuestions mandatoryQuestion = createMandatoryQuestion(true, true, true, true, true, true, true, true, false);
        final String nextPageUrl = serviceUnderTest.generateNextPageUrl(mandatoryQuestion);
        assertThat(nextPageUrl).isEqualTo("/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-funding-location");
    }

    @Test
    public void testGenerateNextPageUrl_fundingAmountConditionsMet() {
        final GrantMandatoryQuestions mandatoryQuestion = createMandatoryQuestion(true, true, true, true, true, true, true, false, false);
        final String nextPageUrl = serviceUnderTest.generateNextPageUrl(mandatoryQuestion);
        assertThat(nextPageUrl).isEqualTo("/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-funding-amount");
    }

    @Test
    public void testGenerateNextPageUrl_charityCommissionNumberConditionsMet() {
        final GrantMandatoryQuestions mandatoryQuestion = createMandatoryQuestion(true, true, true, true, true, true, false, false, false);
        final String nextPageUrl = serviceUnderTest.generateNextPageUrl(mandatoryQuestion);
        assertThat(nextPageUrl).isEqualTo("/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-charity-commission-number");
    }

    @Test
    public void testGenerateNextPageUrl_companiesHouseNumberConditionsMet() {
        final GrantMandatoryQuestions mandatoryQuestion = createMandatoryQuestion(true, true, true, true, true, false, false, false, false);
        final String nextPageUrl = serviceUnderTest.generateNextPageUrl(mandatoryQuestion);
        assertThat(nextPageUrl).isEqualTo("/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-companies-house-number");
    }

    @Test
    public void testGenerateNextPageUrl_orgTypeConditionsMet() {
        final GrantMandatoryQuestions mandatoryQuestion = createMandatoryQuestion(true, true, true, true, false, false, false, false, false);
        final String nextPageUrl = serviceUnderTest.generateNextPageUrl(mandatoryQuestion);
        assertThat(nextPageUrl).isEqualTo("/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-type");
    }

    @Test
    public void testGenerateNextPageUrl_addressConditionsMet() {
        final GrantMandatoryQuestions mandatoryQuestion = createMandatoryQuestion(true, false, false, false, false, false, false, false, false);
        final String nextPageUrl = serviceUnderTest.generateNextPageUrl(mandatoryQuestion);
        assertThat(nextPageUrl).isEqualTo("/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-address");
    }

    @Test
    public void testGenerateNextPageUrl_NoConditionsMet() {
        final GrantMandatoryQuestions mandatoryQuestion = createMandatoryQuestion(false, false, false, false, false, false, false, false, false);
        final String nextPageUrl = serviceUnderTest.generateNextPageUrl(mandatoryQuestion);
        assertThat(nextPageUrl).isEmpty();
    }

    @Test
    public void testGetMandatoryQuestionKeyFromUrl_ValidUrl() {
        final Map<String, Object> mapper = createMapper();
        final String url = "/mandatory-questions/123/organisation-name?queryParam=1";
        final Object questionKey = serviceUnderTest.getMandatoryQuestionKeyFromUrl(url, mapper);
        assertThat(questionKey).isEqualTo("name");
    }

    @Test
    public void testGetMandatoryQuestionKeyFromUrl_AddressValidUrl() {
        final Map<String, Object> mapper = createMapper();
        final String url = "/mandatory-questions/123/organisation-address?queryParam=1";
        final Object questionKey = serviceUnderTest.getMandatoryQuestionKeyFromUrl(url, mapper);
        assertThat(questionKey).isEqualTo(new String[]{"addressLine1", "city", "postcode"});
    }

    @Test
    public void testGetMandatoryQuestionKeyFromUrl_InvalidUrl() {
        final Map<String, Object> mapper = createMapper();
        final String url = "/other-url";
        final Object questionKey = serviceUnderTest.getMandatoryQuestionKeyFromUrl(url, mapper);
        assertThat(questionKey).isNull();
    }

    @Test
    public void testGetValueFromKey_Name() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "name";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo("SampleName");
    }

    @Test
    public void testGetValueFromKey_AddressLine1() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "addressLine1";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo("SampleAddress");
    }

    @Test
    public void testGetValueFromKey_City() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "city";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo("SampleCity");
    }

    @Test
    public void testGetValueFromKey_Postcode() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "postcode";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo("SamplePostcode");
    }

    @Test
    public void testGetValueFromKey_OrgType() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "orgType";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo("SampleOrgType");
    }

    @Test
    public void testGetValueFromKey_CompaniesHouseNumber() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "companiesHouseNumber";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo("SampleCompaniesHouse");
    }

    @Test
    public void testGetValueFromKey_CharityCommissionNumber() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "charityCommissionNumber";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo("SampleCharityCommission");
    }

    @Test
    public void testGetValueFromKey_FundingAmount() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "fundingAmount";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo("100");
    }

    @Test
    public void testGetValueFromKey_FundingLocation() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "fundingLocation";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isEqualTo(List.of("Scotland"));
    }

    @Test
    public void testGetValueFromKey_InvalidKey() {
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDto();
        final String key = "invalidKey";
        final Object value = serviceUnderTest.getValueFromKey(mandatoryQuestionDto, key);
        assertThat(value).isNull();
    }

    @Test
    public void testIsPageAlreadyAnswered_NameAnswered() {
        final String url = "/mandatory-questions/123/organisation-name";
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDtoDynamic("SampleName", null, null, null, null, null, null, null, List.of());
        final boolean result = serviceUnderTest.isPageAlreadyAnswered(url, mandatoryQuestionDto);
        assertThat(result).isTrue();
    }

    @Test
    public void testIsPageAlreadyAnswered_AddressAnswered() {
        final String url = "/mandatory-questions/123/organisation-address";
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDtoDynamic(null, "SampleAddress", "SampleCity", "SamplePostcode", null, null, null, null, List.of());
        final boolean result = serviceUnderTest.isPageAlreadyAnswered(url, mandatoryQuestionDto);
        assertThat(result).isTrue();
    }

    @Test
    public void testIsPageAlreadyAnswered_MultipleKeysAnswered() {
        final String url = "/mandatory-questions/123/organisation-address";
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDtoDynamic("SampleName", "SampleAddress", "SampleCity", "SamplePostcode", null, null, null, null, List.of());
        final boolean result = serviceUnderTest.isPageAlreadyAnswered(url, mandatoryQuestionDto);
        assertThat(result).isTrue();
    }

    @Test
    public void testIsPageAlreadyAnswered_InvalidUrl() {
        final String url = "/other-url";
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDtoDynamic(null, null, null, null, null, null, null, null, List.of());
        final boolean result = serviceUnderTest.isPageAlreadyAnswered(url, mandatoryQuestionDto);
        assertThat(result).isFalse();
    }

    @Test
    public void testIsPageAlreadyAnswered_KeysNotAnswered() {
        final String url = "/mandatory-questions/123/organisation-address";
        final GetGrantMandatoryQuestionDto mandatoryQuestionDto = createMandatoryQuestionDtoDynamic("SampleName", null, "SampleCity", "SamplePostcode", null, null, null, null, List.of());
        final boolean result = serviceUnderTest.isPageAlreadyAnswered(url, mandatoryQuestionDto);
        assertThat(result).isFalse();
    }

    private GetGrantMandatoryQuestionDto createMandatoryQuestionDtoDynamic(String name, String addressLine1, String city, String postcode, String orgType, String companiesHouseNumber, String charityCommissionNumber, String fundingAmount, List<String> fundingLocation) {
        GetGrantMandatoryQuestionDto dto = new GetGrantMandatoryQuestionDto();
        dto.setName(name);
        dto.setAddressLine1(addressLine1);
        dto.setCity(city);
        dto.setPostcode(postcode);
        dto.setOrgType(orgType);
        dto.setCompaniesHouseNumber(companiesHouseNumber);
        dto.setCharityCommissionNumber(charityCommissionNumber);
        dto.setFundingAmount(fundingAmount);
        dto.setFundingLocation(fundingLocation);
        return dto;
    }


    private Map<String, Object> createMapper() {
        final Map<String, Object> mapper = new HashMap<>();
        mapper.put("organisation-name", "name");
        mapper.put("organisation-address", new String[]{"addressLine1", "city", "postcode"});
        return mapper;
    }

    private GetGrantMandatoryQuestionDto createMandatoryQuestionDto() {
        GetGrantMandatoryQuestionDto dto = new GetGrantMandatoryQuestionDto();
        dto.setName("SampleName");
        dto.setAddressLine1("SampleAddress");
        dto.setCity("SampleCity");
        dto.setPostcode("SamplePostcode");
        dto.setOrgType("SampleOrgType");
        dto.setCompaniesHouseNumber("SampleCompaniesHouse");
        dto.setCharityCommissionNumber("SampleCharityCommission");
        dto.setFundingAmount("100");
        dto.setFundingLocation(List.of("Scotland"));

        return dto;
    }

    private GrantMandatoryQuestions createMandatoryQuestion(boolean name, boolean addressLine1, boolean city, boolean postcode, boolean orgType, boolean companiesHouseNumber, boolean charityCommissionNumber, boolean fundingAmount, boolean fundingLocation) {
        final GrantMandatoryQuestions mandatoryQuestion = new GrantMandatoryQuestions();
        mandatoryQuestion.setId(MANDATORY_QUESTION_ID);
        mandatoryQuestion.setName(name ? "SampleName" : null);
        mandatoryQuestion.setAddressLine1(addressLine1 ? "SampleAddress" : null);
        mandatoryQuestion.setCity(city ? "SampleCity" : null);
        mandatoryQuestion.setPostcode(postcode ? "SamplePostcode" : null);
        mandatoryQuestion.setOrgType(orgType ? GrantMandatoryQuestionOrgType.LIMITED_COMPANY : null);
        mandatoryQuestion.setCompaniesHouseNumber(companiesHouseNumber ? "SampleCompaniesHouse" : null);
        mandatoryQuestion.setCharityCommissionNumber(charityCommissionNumber ? "SampleCharityCommission" : null);
        mandatoryQuestion.setFundingAmount(fundingAmount ? new BigDecimal(100) : null);
        mandatoryQuestion.setFundingLocation(fundingLocation ? new GrantMandatoryQuestionFundingLocation[]{GrantMandatoryQuestionFundingLocation.SCOTLAND} : null);
        return mandatoryQuestion;
    }
}