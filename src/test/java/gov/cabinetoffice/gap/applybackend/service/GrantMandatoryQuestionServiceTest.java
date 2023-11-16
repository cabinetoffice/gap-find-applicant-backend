package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.constants.MandatoryQuestionConstants;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionStatus;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.mapper.GrantApplicantOrganisationProfileMapper;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantMandatoryQuestionServiceTest {

    final ArgumentCaptor<GrantMandatoryQuestions> captor = ArgumentCaptor.forClass(GrantMandatoryQuestions.class);
    private final String applicantUserId = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";
    private final UUID MANDATORY_QUESTION_ID = UUID.fromString("8e33d655-556e-49d5-bc46-3cfa4fdfa00f");

    private final GrantApplicant grantApplicant = GrantApplicant.builder().id(1).build();

    private final GrantApplicantOrganisationProfile organisationProfile = GrantApplicantOrganisationProfile
            .builder()
            .id(1)
            .legalName("legalName")
            .addressLine1("addressLine1")
            .town("town")
            .postcode("postcode")
            .build();
    @Mock
    private GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;
    @Mock
    private GrantApplicantOrganisationProfileMapper organisationProfileMapper;
    @Mock
    private EnvironmentProperties envProperties;

    @Spy
    @InjectMocks
    private GrantMandatoryQuestionService serviceUnderTest;

    @Nested
    class getGrantMandatoryQuestionById {
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
    }

    @Nested
    class getGrantMandatoryQuestionBySubmissionId {
        @Test
        void getGrantMandatoryQuestionBySubmissionId_ThrowsNotFoundException() {
            final UUID submissionId = UUID.randomUUID();

            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenThrow(NotFoundException.class);

            assertThrows(NotFoundException.class, () -> serviceUnderTest.getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submissionId, applicantUserId));
        }

        @Test
        void getGrantMandatoryQuestionBySubmissionId_ThrowsForbiddenException() {

            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .build();

            final Optional<GrantMandatoryQuestions> mandatoryQuestions = Optional.of(GrantMandatoryQuestions
                    .builder()
                    .createdBy(applicant)
                    .build());

            final UUID submissionId = UUID.randomUUID();
            final String invalidUserId = "a-bad-user-id";

            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenReturn(mandatoryQuestions);

            assertThrows(ForbiddenException.class, () -> serviceUnderTest.getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submissionId, invalidUserId));
        }

        @Test
        void getGrantMandatoryQuestionBySubmissionId_ReturnsExpectedMandatoryQuestions() {

            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .build();

            final Optional<GrantMandatoryQuestions> mandatoryQuestions = Optional.of(GrantMandatoryQuestions
                    .builder()
                    .createdBy(applicant)
                    .build());

            final UUID submissionId = UUID.randomUUID();

            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenReturn(mandatoryQuestions);

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submissionId, applicantUserId);

            assertThat(methodResponse).isEqualTo(mandatoryQuestions.get());
        }
    }

    @Nested
    class getGrantMandatoryQuestionByScheme {
        @Test
        void getMandatoryQuestionByScheme_ThrowsNotFoundException() {
            final GrantScheme scheme = new GrantScheme();
            final String applicantSub = "valid-applicant-id";

            when(grantMandatoryQuestionRepository.findByGrantScheme_IdAndCreatedBy_UserId(1, applicantSub))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> serviceUnderTest.getMandatoryQuestionBySchemeId(1, applicantSub));
        }

        @Test
        void getMandatoryQuestionByScheme_ThrowsForbiddenException() {
            final GrantScheme scheme = new GrantScheme();
            final String applicantSub = "valid-applicant-id";

            final GrantApplicant createdByOtherUser = GrantApplicant.builder().userId("other-user-id").build();
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder().createdBy(createdByOtherUser).build();

            when(grantMandatoryQuestionRepository.findByGrantScheme_IdAndCreatedBy_UserId(1, applicantSub))
                    .thenReturn(Optional.of(mandatoryQuestions));

            assertThrows(ForbiddenException.class, () -> serviceUnderTest.getMandatoryQuestionBySchemeId(1, applicantSub));
        }

        @Test
        void getMandatoryQuestionByScheme_ReturnsExpectedMandatoryQuestions() {
            final GrantScheme scheme = new GrantScheme();
            final String applicantSub = "valid-applicant-id";

            final GrantApplicant createdByValidUser = GrantApplicant.builder().userId(applicantSub).build();
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder().createdBy(createdByValidUser).build();

            when(grantMandatoryQuestionRepository.findByGrantScheme_IdAndCreatedBy_UserId(1, applicantSub))
                    .thenReturn(Optional.of(mandatoryQuestions));

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.getMandatoryQuestionBySchemeId(1, applicantSub);

            assertThat(methodResponse).isEqualTo(mandatoryQuestions);
        }

    }

    @Nested
    class createMandatoryQuestion {
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

            when(grantMandatoryQuestionRepository.existsByGrantScheme_IdAndCreatedBy_Id(scheme.getId(), applicant.getId()))
                    .thenReturn(true);

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
                    .organisationProfile(organisationProfile)
                    .build();

            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder()
                    .grantScheme(scheme)
                    .createdBy(applicant)
                    .city(organisationProfile.getTown())
                    .name(organisationProfile.getLegalName())
                    .postcode(organisationProfile.getPostcode())
                    .addressLine1(organisationProfile.getAddressLine1())
                    .build();

            when(grantMandatoryQuestionRepository.existsByGrantScheme_IdAndCreatedBy_Id(scheme.getId(), applicant.getId()))
                    .thenReturn(false);

            when(grantMandatoryQuestionRepository.save(Mockito.any()))
                    .thenReturn(grantMandatoryQuestions);

            when(organisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(organisationProfile))
                    .thenReturn(grantMandatoryQuestions);

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.createMandatoryQuestion(scheme, applicant);

            verify(organisationProfileMapper).mapOrgProfileToGrantMandatoryQuestion(organisationProfile);
            verify(grantMandatoryQuestionRepository).save(any());
            assertThat(methodResponse).isEqualTo(grantMandatoryQuestions);
        }

        @Test
        void createMandatoryQuestion_NullsCCandCH_ifTooLong() {

            final GrantScheme scheme = GrantScheme
                    .builder()
                    .id(1)
                    .build();

            final GrantApplicantOrganisationProfile orgProfileWithLongCCandCH = GrantApplicantOrganisationProfile
                    .builder()
                    .id(1)
                    .legalName("legalName")
                    .addressLine1("addressLine1")
                    .town("town")
                    .postcode("postcode")
                    .companiesHouseNumber("THIS IS A REALLY LONG COMPANIES HOUSE NUMEBR")
                    .charityCommissionNumber("THIS IS A REALLY LONG CHARITY COMISSION NUMBE")
                    .build();

            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .organisationProfile(orgProfileWithLongCCandCH)
                    .build();

            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder()
                    .grantScheme(scheme)
                    .createdBy(applicant)
                    .city(orgProfileWithLongCCandCH.getTown())
                    .name(orgProfileWithLongCCandCH.getLegalName())
                    .postcode(orgProfileWithLongCCandCH.getPostcode())
                    .addressLine1(orgProfileWithLongCCandCH.getAddressLine1())
                    .companiesHouseNumber(orgProfileWithLongCCandCH.getCompaniesHouseNumber())
                    .charityCommissionNumber(orgProfileWithLongCCandCH.getCharityCommissionNumber())
                    .build();

            when(grantMandatoryQuestionRepository.save(Mockito.any()))
                    .thenReturn(grantMandatoryQuestions);

            when(organisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(orgProfileWithLongCCandCH))
                    .thenReturn(grantMandatoryQuestions);

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.createMandatoryQuestion(scheme, applicant);

            verify(organisationProfileMapper).mapOrgProfileToGrantMandatoryQuestion(orgProfileWithLongCCandCH);
            verify(grantMandatoryQuestionRepository).save(any());

            assertThat(methodResponse.getCharityCommissionNumber()).isNull();
            assertThat(methodResponse.getCompaniesHouseNumber()).isNull();
        }

    }

    @Nested
    class updateMandatoryQuestions {
        @Test
        void updateMandatoryQuestion_ThrowsNotFoundException() {
            final UUID mandatoryQuestionsId = UUID.randomUUID();

            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                    .builder()
                    .id(mandatoryQuestionsId)
                    .build();

            when(grantMandatoryQuestionRepository.findById(mandatoryQuestionsId))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> serviceUnderTest.updateMandatoryQuestion(grantMandatoryQuestions, grantApplicant));
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

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.updateMandatoryQuestion(grantMandatoryQuestions, grantApplicant);

            verify(grantMandatoryQuestionRepository).save(grantMandatoryQuestions);
            assertThat(methodResponse).isEqualTo(grantMandatoryQuestions);
        }

        @Test
        void updateMandatoryQuestion_UpdatesExpectedMandatoryQuestionsAndSetsGapId() {
            final UUID mandatoryQuestionsId = UUID.randomUUID();
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                    .builder()
                    .id(mandatoryQuestionsId)
                    .status(GrantMandatoryQuestionStatus.COMPLETED)
                    .build();

            when(envProperties.getEnvironmentName()).thenReturn("local");
            when(grantMandatoryQuestionRepository.findById(mandatoryQuestionsId))
                    .thenReturn(Optional.of(grantMandatoryQuestions));
            when(grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                    .thenReturn(grantMandatoryQuestions);
            when(grantMandatoryQuestionRepository.count()).thenReturn(Long.valueOf(2));

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.updateMandatoryQuestion(grantMandatoryQuestions, grantApplicant);

            verify(grantMandatoryQuestionRepository).save(grantMandatoryQuestions);
            assertThat(methodResponse).isEqualTo(grantMandatoryQuestions);
            assertThat(methodResponse.getGapId()).contains("GAP-local-MQ-");
            assertThat(methodResponse.getGapId()).isEqualTo(grantMandatoryQuestions.getGapId());
        }

        @Test
        void updateMandatoryQuestion_UpdatesExpectedMandatoryQuestionsAndSetsGapIdBySubmission() {
            final UUID mandatoryQuestionsId = UUID.randomUUID();
            final Submission submission = Submission.builder().gapId("GAP-ID").build();
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                    .builder()
                    .id(mandatoryQuestionsId)
                    .status(GrantMandatoryQuestionStatus.COMPLETED)
                    .submission(submission)
                    .build();

            when(grantMandatoryQuestionRepository.findById(mandatoryQuestionsId))
                    .thenReturn(Optional.of(grantMandatoryQuestions));
            when(grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                    .thenReturn(grantMandatoryQuestions);

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.updateMandatoryQuestion(grantMandatoryQuestions, grantApplicant);

            verify(grantMandatoryQuestionRepository).save(grantMandatoryQuestions);
            assertThat(methodResponse).isEqualTo(grantMandatoryQuestions);
            assertThat(methodResponse.getGapId()).isEqualTo(submission.getGapId());
        }
    }

    @Nested
    class generateNextPageUrl {
        @Test
        void testGenerateNextPageUrl() {
            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .build();
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                    .builder()
                    .id(MANDATORY_QUESTION_ID)
                    .createdBy(applicant)
                    .build();
            when(grantMandatoryQuestionRepository.findById(MANDATORY_QUESTION_ID))
                    .thenReturn(Optional.of(grantMandatoryQuestions));

            final String url = "/any/url/organisation-type?some-param=some-value";
            final String expectedNextPageUrl = "/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-name";

            final String nextPageUrl = serviceUnderTest.generateNextPageUrl(url, MANDATORY_QUESTION_ID, applicantUserId);

            assertThat(nextPageUrl).isEqualTo(expectedNextPageUrl);
        }

        @Test
        void testGenerateNextPageUrlForSkippingCompaniesHouseAndCharityCommission() {
            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .build();
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                    .builder()
                    .id(MANDATORY_QUESTION_ID)
                    .createdBy(applicant)
                    .orgType(GrantMandatoryQuestionOrgType.INDIVIDUAL)
                    .build();
            when(grantMandatoryQuestionRepository.findById(MANDATORY_QUESTION_ID))
                    .thenReturn(Optional.of(grantMandatoryQuestions));

            final String url = "/any/url/organisation-address?some-param=some-value";
            final String expectedNextPageUrl = "/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-funding-amount";

            final String nextPageUrl = serviceUnderTest.generateNextPageUrl(url, MANDATORY_QUESTION_ID, applicantUserId);

            assertThat(nextPageUrl).isEqualTo(expectedNextPageUrl);
        }

        @Test
        void testGenerateNextPageUrlForNotSkippingCompaniesHouseAndCharityCommission() {
            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .build();
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                    .builder()
                    .id(MANDATORY_QUESTION_ID)
                    .createdBy(applicant)
                    .orgType(GrantMandatoryQuestionOrgType.CHARITY)
                    .build();
            when(grantMandatoryQuestionRepository.findById(MANDATORY_QUESTION_ID))
                    .thenReturn(Optional.of(grantMandatoryQuestions));

            final String url = "/any/url/organisation-address?some-param=some-value";
            final String expectedNextPageUrl = "/mandatory-questions/" + MANDATORY_QUESTION_ID + "/organisation-companies-house-number";

            final String nextPageUrl = serviceUnderTest.generateNextPageUrl(url, MANDATORY_QUESTION_ID, applicantUserId);

            assertThat(nextPageUrl).isEqualTo(expectedNextPageUrl);
        }

        @Test
        void testGenerateNextPageUrl_UrlNotInMapper() {
            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .build();
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                    .builder()
                    .id(MANDATORY_QUESTION_ID)
                    .createdBy(applicant)
                    .build();
            when(grantMandatoryQuestionRepository.findById(MANDATORY_QUESTION_ID))
                    .thenReturn(Optional.of(grantMandatoryQuestions));

            final String url = "/any/url/thatIsNotInMapper";
            final String expectedNextPageUrl = "";

            final String nextPageUrl = serviceUnderTest.generateNextPageUrl(url, MANDATORY_QUESTION_ID, applicantUserId);

            assertThat(nextPageUrl).isEqualTo(expectedNextPageUrl);
        }
    }

    @Nested
    class addMandatoryQuestionsToSubmissionObject {
        //TODO I think we could maybe write more thorough tests for this method but these should be OK for now

        @Test
        void doesNothing_IfSubmissionIsNull() {
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder().build();
            assertThat(mandatoryQuestions.getSubmission()).isNull();

            serviceUnderTest.addMandatoryQuestionsToSubmissionObject(mandatoryQuestions);

            // Strange test this, but basically if we never call these methods and no exception is thrown then we're happy
            verify(serviceUnderTest, never()).buildOrganisationDetailsSubmissionSection(Mockito.any(), Mockito.any());
            verify(serviceUnderTest, never()).buildFundingDetailsSubmissionSection(Mockito.any(), Mockito.any());

            // probably worth making sure the submission is still  null for good measure
            assertThat(mandatoryQuestions.getSubmission()).isNull();
        }

        @Test
        void doesNothing_IfSubmissionIsVersionOne() {

            final Submission submission = Submission.builder()
                    .version(1)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .submission(submission)
                    .build();

            serviceUnderTest.addMandatoryQuestionsToSubmissionObject(mandatoryQuestions);

            // Strange test this, but basically if we never call these methods and no exception is thrown then we're happy
            verify(serviceUnderTest, never()).buildOrganisationDetailsSubmissionSection(Mockito.any(), Mockito.any());
            verify(serviceUnderTest, never()).buildFundingDetailsSubmissionSection(Mockito.any(), Mockito.any());
        }

        @Test
        void throwsNotFoundException_IfOrganisationDetailsSectionIsNotFound() {

            final SubmissionDefinition definition = SubmissionDefinition.builder()
                    .sections(Collections.emptyList())
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .version(2)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .submission(submission)
                    .build();

            assertThatThrownBy(() -> serviceUnderTest.addMandatoryQuestionsToSubmissionObject(mandatoryQuestions))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("No Section with ID ORGANISATION_DETAILS was found");
        }

        @Test
        void throwsNotFoundException_IfFundingDetailsSectionIsNotFound() {

            final SubmissionSection organisationDetails = SubmissionSection.builder()
                    .sectionId("ORGANISATION_DETAILS")
                    .build();

            final SubmissionDefinition definition = SubmissionDefinition.builder()
                    .sections(List.of(organisationDetails))
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .version(2)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .submission(submission)
                    .build();

            assertThatThrownBy(() -> serviceUnderTest.addMandatoryQuestionsToSubmissionObject(mandatoryQuestions))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("No Section with ID FUNDING_DETAILS was found");
        }

        @Test
        void addMandatoryQuestions() {

            final SubmissionSection eligibility = SubmissionSection.builder()
                    .sectionId("ELIGIBILITY")
                    .build();

            final SubmissionSection organisationDetails = SubmissionSection.builder()
                    .sectionId("ORGANISATION_DETAILS")
                    .build();

            final SubmissionSection fundingDetails = SubmissionSection.builder()
                    .sectionId("FUNDING_DETAILS")
                    .build();

            final SubmissionDefinition definition = SubmissionDefinition.builder()
                    .sections(new ArrayList(List.of(eligibility, organisationDetails, fundingDetails)))
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .version(2)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .submission(submission)
                    .name("AND Digital")
                    .addressLine1("215 Bothwell Street")
                    .city("Glasgow")
                    .postcode("G2 7EZ")
                    .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
                    .fundingAmount(BigDecimal.valueOf(150000))
                    .fundingLocation(new GrantMandatoryQuestionFundingLocation[]{
                            GrantMandatoryQuestionFundingLocation.SCOTLAND
                    })
                    .companiesHouseNumber("1234567")
                    .charityCommissionNumber("22135")
                    .build();

            serviceUnderTest.addMandatoryQuestionsToSubmissionObject(mandatoryQuestions);

            verify(serviceUnderTest).buildOrganisationDetailsSubmissionSection(Mockito.any(), Mockito.any());
            verify(serviceUnderTest).buildFundingDetailsSubmissionSection(Mockito.any(), Mockito.any());
        }
    }

    @Nested
    class buildOrganisationDetailsSubmissionSection {

        @Test
        void addsCorrectDetails() {

            final SubmissionSectionStatus status = SubmissionSectionStatus.IN_PROGRESS;

            final SubmissionSection eligibility = SubmissionSection.builder()
                    .sectionId("ELIGIBILITY")
                    .build();

            final SubmissionSection organisationDetails = SubmissionSection.builder()
                    .sectionId("ORGANISATION_DETAILS")
                    .build();

            final SubmissionSection fundingDetails = SubmissionSection.builder()
                    .sectionId("FUNDING_DETAILS")
                    .build();

            final SubmissionDefinition definition = SubmissionDefinition.builder()
                    .sections(new ArrayList(List.of(eligibility, organisationDetails, fundingDetails)))
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .submission(submission)
                    .build();

            final SubmissionQuestion orgName = SubmissionQuestion.builder()
                    .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_NAME.toString())
                    .build();

            final SubmissionQuestion applicantType = SubmissionQuestion.builder()
                    .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString())
                    .build();

            final SubmissionQuestion orgAddress = SubmissionQuestion.builder()
                    .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_ADDRESS.toString())
                    .build();

            final SubmissionQuestion charityNumber = SubmissionQuestion.builder()
                    .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_CHARITY_NUMBER.toString())
                    .build();

            final SubmissionQuestion companiesHouse = SubmissionQuestion.builder()
                    .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_CHARITY_NUMBER.toString())
                    .build();

            doReturn(orgName)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_NAME.toString(), mandatoryQuestions);

            doReturn(applicantType)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString(), mandatoryQuestions);

            doReturn(orgAddress)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_ADDRESS.toString(), mandatoryQuestions);

            doReturn(charityNumber)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_CHARITY_NUMBER.toString(), mandatoryQuestions);

            doReturn(companiesHouse)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_COMPANIES_HOUSE.toString(), mandatoryQuestions);


            final SubmissionSection orgDetailsSection = serviceUnderTest.buildOrganisationDetailsSubmissionSection(mandatoryQuestions, status);


            assertThat(orgDetailsSection.getSectionId()).isEqualTo(MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID);
            assertThat(orgDetailsSection.getSectionTitle()).isEqualTo(MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_TITLE);
            assertThat(orgDetailsSection.getSectionStatus()).isEqualTo(status);
            assertThat(orgDetailsSection.getQuestions()).containsExactlyElementsOf(List.of(
                    orgName,
                    applicantType,
                    orgAddress,
                    charityNumber,
                    companiesHouse
            ));
        }
    }

    @Nested
    class buildFundingDetailsSubmissionSection {
        @Test
        void addsCorrectDetails() {

            final SubmissionSectionStatus status = SubmissionSectionStatus.IN_PROGRESS;

            final SubmissionSection eligibility = SubmissionSection.builder()
                    .sectionId("ELIGIBILITY")
                    .build();

            final SubmissionSection organisationDetails = SubmissionSection.builder()
                    .sectionId("ORGANISATION_DETAILS")
                    .build();

            final SubmissionSection fundingDetails = SubmissionSection.builder()
                    .sectionId("FUNDING_DETAILS")
                    .build();

            final SubmissionDefinition definition = SubmissionDefinition.builder()
                    .sections(new ArrayList(List.of(eligibility, organisationDetails, fundingDetails)))
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .submission(submission)
                    .build();

            final SubmissionQuestion applicantAmount = SubmissionQuestion.builder()
                    .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_AMOUNT.toString())
                    .build();

            final SubmissionQuestion beneficiaryLocation = SubmissionQuestion.builder()
                    .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.BENEFITIARY_LOCATION.toString())
                    .build();


            doReturn(applicantAmount)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_AMOUNT.toString(), mandatoryQuestions);

            doReturn(beneficiaryLocation)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.BENEFITIARY_LOCATION.toString(), mandatoryQuestions);


            final SubmissionSection fundingDetailsSection = serviceUnderTest.buildFundingDetailsSubmissionSection(mandatoryQuestions, status);


            assertThat(fundingDetailsSection.getSectionId()).isEqualTo(MandatoryQuestionConstants.FUNDING_DETAILS_SECTION_ID);
            assertThat(fundingDetailsSection.getSectionTitle()).isEqualTo(MandatoryQuestionConstants.FUNDING_DETAILS_SECTION_TITLE);
            assertThat(fundingDetailsSection.getSectionStatus()).isEqualTo(status);
            assertThat(fundingDetailsSection.getQuestions()).containsExactlyElementsOf(List.of(
                    applicantAmount,
                    beneficiaryLocation
            ));
        }
    }

    @Nested
    class mandatoryQuestionToSubmissionQuestion {

        static final String orgName = "AND Digital";
        static final String address1 = "215 Bothwell Street";
        static final String city = "Glasgow";
        static final String postcode = "G2 7EZ";
        static final GrantMandatoryQuestionOrgType organisationType = GrantMandatoryQuestionOrgType.LIMITED_COMPANY;
        static final BigDecimal fundingAmount = BigDecimal.valueOf(150000);
        static final GrantMandatoryQuestionFundingLocation[] fundingLocations = new GrantMandatoryQuestionFundingLocation[]{
                GrantMandatoryQuestionFundingLocation.SCOTLAND
        };
        static final String charityNumber = "1234567";
        static final String companiesHouseNumber = "22135";

        private static Stream<Arguments> provideArguments() {
            return Stream.of(
                    Arguments.of("APPLICANT_ORG_NAME", orgName),
                    Arguments.of("APPLICANT_TYPE", organisationType.toString()),
                    Arguments.of("APPLICANT_ORG_CHARITY_NUMBER", charityNumber),
                    Arguments.of("APPLICANT_ORG_COMPANIES_HOUSE", companiesHouseNumber),
                    Arguments.of("APPLICANT_AMOUNT", fundingAmount.toString()),
                    Arguments.of("BENEFITIARY_LOCATION", new String[]{
                            GrantMandatoryQuestionFundingLocation.SCOTLAND.getName()
                    }),
                    Arguments.of("APPLICANT_ORG_ADDRESS", new String[]{
                            address1,
                            null,
                            city,
                            null,
                            postcode
                    })
            );
        }


        @ParameterizedTest
        @MethodSource("provideArguments")
        void returnsExpectedQuestionTypeForQuestionId(final String questionId, final Object expectedResponse) {

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .name(orgName)
                    .addressLine1(address1)
                    .city(city)
                    .postcode(postcode)
                    .orgType(organisationType)
                    .fundingAmount(fundingAmount)
                    .fundingLocation(fundingLocations)
                    .companiesHouseNumber(companiesHouseNumber)
                    .charityCommissionNumber(charityNumber)
                    .build();


            final SubmissionQuestion question = serviceUnderTest.mandatoryQuestionToSubmissionQuestion(questionId, mandatoryQuestions);

            assertThat(question.getQuestionId()).isEqualTo(questionId);

            // I am sorry.......
            switch (questionId) {
                case "APPLICANT_ORG_NAME":
                case "APPLICANT_TYPE":
                case "APPLICANT_ORG_CHARITY_NUMBER":
                case "APPLICANT_ORG_COMPANIES_HOUSE":
                case "APPLICANT_AMOUNT":
                    assertThat(question.getResponse()).isEqualTo(expectedResponse);
                    break;
                case "BENEFITIARY_LOCATION":
                case "APPLICANT_ORG_ADDRESS":
                    assertThat(question.getMultiResponse()).isEqualTo(expectedResponse);
                    break;
            }
        }

        @Test
        void throwsIllegalArgumentException_IfQuestionIdNotFound() {

            final String nonexistentQuestionId = "A_NON_EXISTENT_ID";
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder().build();

            assertThatThrownBy(() -> serviceUnderTest.mandatoryQuestionToSubmissionQuestion(nonexistentQuestionId, mandatoryQuestions))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("There is no method to process this question");
        }
    }
}