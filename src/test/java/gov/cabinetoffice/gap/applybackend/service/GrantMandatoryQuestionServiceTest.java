package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.constants.MandatoryQuestionConstants;
import gov.cabinetoffice.gap.applybackend.enums.*;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.GrantApplicationNotPublishedException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.mapper.GrantApplicantOrganisationProfileMapper;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import gov.cabinetoffice.gap.applybackend.repository.SubmissionRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantMandatoryQuestionServiceTest {

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
    private SubmissionRepository submissionRepository;
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

        @Test
        void getGrantMandatoryQuestionBySubmissionId_FallsBackToSchemeWhenNotLinkedToSubmission() {
            // Multi-app: MQ exists but is linked to a different (earlier) submission
            final UUID submissionId = UUID.randomUUID();
            final Integer schemeId = 1;

            final GrantApplicant applicant = GrantApplicant.builder().userId(applicantUserId).build();
            final GrantScheme scheme = GrantScheme.builder().id(schemeId).build();
            final Submission submission = Submission.builder().scheme(scheme).build();

            final Optional<GrantMandatoryQuestions> mandatoryQuestions = Optional.of(GrantMandatoryQuestions
                    .builder()
                    .createdBy(applicant)
                    .build());

            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId)).thenReturn(Optional.empty());
            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId)).thenReturn(Optional.of(submission));
            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(schemeId, applicantUserId)).thenReturn(mandatoryQuestions);

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submissionId, applicantUserId);

            assertThat(methodResponse).isEqualTo(mandatoryQuestions.get());
        }

        @Test
        void getGrantMandatoryQuestionBySubmissionId_ThrowsNotFoundWhenFallbackAlsoFindsNothing() {
            final UUID submissionId = UUID.randomUUID();
            final Integer schemeId = 1;

            final GrantScheme scheme = GrantScheme.builder().id(schemeId).build();
            final Submission submission = Submission.builder().scheme(scheme).build();

            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId)).thenReturn(Optional.empty());
            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId)).thenReturn(Optional.of(submission));
            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(schemeId, applicantUserId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> serviceUnderTest.getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submissionId, applicantUserId));
        }
    }

    @Nested
    class getGrantMandatoryQuestionByScheme {
        @Test
        void getMandatoryQuestionByScheme_ThrowsNotFoundException() {
            final String applicantSub = "valid-applicant-id";

            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(1, applicantSub))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> serviceUnderTest.getMandatoryQuestionBySchemeId(1, applicantSub));
        }

        @Test
        void getMandatoryQuestionByScheme_ThrowsForbiddenException() {
            final String applicantSub = "valid-applicant-id";

            final GrantApplicant createdByOtherUser = GrantApplicant.builder().userId("other-user-id").build();
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder().createdBy(createdByOtherUser).build();

            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(1, applicantSub))
                    .thenReturn(Optional.of(mandatoryQuestions));

            assertThrows(ForbiddenException.class, () -> serviceUnderTest.getMandatoryQuestionBySchemeId(1, applicantSub));
        }

        @Test
        void getMandatoryQuestionByScheme_ReturnsExpectedMandatoryQuestions() {
            final String applicantSub = "valid-applicant-id";

            final GrantApplicant createdByValidUser = GrantApplicant.builder().userId(applicantSub).build();
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder().createdBy(createdByValidUser).build();

            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(1, applicantSub))
                    .thenReturn(Optional.of(mandatoryQuestions));

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.getMandatoryQuestionBySchemeId(1, applicantSub);

            assertThat(methodResponse).isEqualTo(mandatoryQuestions);
        }

        @Test
        void getMandatoryQuestionByScheme_PrefersMostRecentCompletedMandatoryQuestion() {
            final String applicantSub = "valid-applicant-id";

            final GrantApplicant createdByValidUser = GrantApplicant.builder().userId(applicantSub).build();
            final GrantMandatoryQuestions completedMandatoryQuestion = GrantMandatoryQuestions.builder()
                    .createdBy(createdByValidUser)
                    .status(GrantMandatoryQuestionStatus.COMPLETED)
                    .build();

            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdAndStatusOrderByCreatedDesc(
                    1, applicantSub, GrantMandatoryQuestionStatus.COMPLETED))
                    .thenReturn(Optional.of(completedMandatoryQuestion));

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.getMandatoryQuestionBySchemeId(1, applicantSub);

            assertThat(methodResponse).isEqualTo(completedMandatoryQuestion);
            // A completed MQ wins outright: the submission-linked and most-recent-overall lookups must not be reached.
            verify(grantMandatoryQuestionRepository, never())
                    .findFirstByGrantScheme_IdAndCreatedBy_UserIdAndSubmissionIsNotNullOrderByCreatedDesc(Mockito.anyInt(), Mockito.anyString());
            verify(grantMandatoryQuestionRepository, never())
                    .findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(Mockito.anyInt(), Mockito.anyString());
        }

        @Test
        void getMandatoryQuestionByScheme_FallsBackToSubmissionLinkedWhenNoCompletedExists() {
            final String applicantSub = "valid-applicant-id";

            final GrantApplicant createdByValidUser = GrantApplicant.builder().userId(applicantSub).build();
            final GrantMandatoryQuestions submissionLinkedMandatoryQuestion = GrantMandatoryQuestions.builder()
                    .createdBy(createdByValidUser)
                    .status(GrantMandatoryQuestionStatus.IN_PROGRESS)
                    .submission(Submission.builder().build())
                    .build();

            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdAndStatusOrderByCreatedDesc(
                    1, applicantSub, GrantMandatoryQuestionStatus.COMPLETED))
                    .thenReturn(Optional.empty());
            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdAndSubmissionIsNotNullOrderByCreatedDesc(
                    1, applicantSub))
                    .thenReturn(Optional.of(submissionLinkedMandatoryQuestion));

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.getMandatoryQuestionBySchemeId(1, applicantSub);

            assertThat(methodResponse).isEqualTo(submissionLinkedMandatoryQuestion);
            // With no completed MQ but a submission-linked one, the most-recent-overall lookup must not be reached.
            verify(grantMandatoryQuestionRepository, never())
                    .findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(Mockito.anyInt(), Mockito.anyString());
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

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.createMandatoryQuestion(scheme, applicant, false);

            verify(grantMandatoryQuestionRepository, never()).save(Mockito.any());
            assertThat(methodResponse).isEqualTo(existingMandatoryQuestions);
        }

        @Test
        void createMandatoryQuestion_ThrowsErrorIfApplicationClosed() {
            final GrantScheme scheme = GrantScheme
                    .builder()
                    .id(1)
                    .grantApplication(GrantApplication.builder().applicationStatus(GrantApplicationStatus.REMOVED).build())
                    .build();

            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .organisationProfile(organisationProfile)
                    .build();

            when(grantMandatoryQuestionRepository.existsByGrantScheme_IdAndCreatedBy_Id(scheme.getId(), applicant.getId()))
                    .thenReturn(false);

            assertThrows(GrantApplicationNotPublishedException.class, () -> serviceUnderTest.createMandatoryQuestion(scheme, applicant, true));
        }


        @Test
        void createMandatoryQuestion_CreatesNewEntry_IfNoExistingQuestionsFound() {

            final GrantScheme scheme = GrantScheme
                    .builder()
                    .id(1)
                    .grantApplication(GrantApplication.builder().applicationStatus(GrantApplicationStatus.PUBLISHED).build())
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

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.createMandatoryQuestion(scheme, applicant,true);

            verify(organisationProfileMapper).mapOrgProfileToGrantMandatoryQuestion(organisationProfile);
            verify(grantMandatoryQuestionRepository).save(any());
            assertThat(methodResponse).isEqualTo(grantMandatoryQuestions);
        }

        @Test
        void createMandatoryQuestion_NullsCCandCH_ifTooLong() {

            final GrantScheme scheme = GrantScheme
                    .builder()
                    .grantApplication(GrantApplication.builder().applicationStatus(GrantApplicationStatus.PUBLISHED).build())
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

            final GrantMandatoryQuestions methodResponse = serviceUnderTest.createMandatoryQuestion(scheme, applicant,true);

            verify(organisationProfileMapper).mapOrgProfileToGrantMandatoryQuestion(orgProfileWithLongCCandCH);
            verify(grantMandatoryQuestionRepository).save(any());

            assertThat(methodResponse.getCharityCommissionNumber()).isNull();
            assertThat(methodResponse.getCompaniesHouseNumber()).isNull();
        }

    }

    @Nested
    class ensureMandatoryQuestionForSubmission {

        private Submission buildNewSubmissionWithoutOrgInDefinition(final UUID submissionId, final GrantScheme scheme, final GrantApplicant applicant) {
            final SubmissionDefinition definition = SubmissionDefinition.builder()
                    .sections(new ArrayList<>(List.of(
                            SubmissionSection.builder().sectionId("ELIGIBILITY").build(),
                            SubmissionSection.builder().sectionId("ORGANISATION_DETAILS").build(),
                            SubmissionSection.builder().sectionId("FUNDING_DETAILS").build()
                    )))
                    .build();
            return Submission.builder()
                    .id(submissionId)
                    .definition(definition)
                    .version(2)
                    .scheme(scheme)
                    .applicant(applicant)
                    .build();
        }

        private Submission buildBrokenSiblingSubmission(final UUID submissionId, final GrantScheme scheme,
                final GrantApplicant applicant, final SubmissionStatus status) {
            final SubmissionSection organisationDetails = SubmissionSection.builder()
                    .sectionId("ORGANISATION_DETAILS")
                    .questions(new ArrayList<>(List.of(
                            SubmissionQuestion.builder().questionId("APPLICANT_TYPE").response("Limited company").build(),
                            SubmissionQuestion.builder().questionId("APPLICANT_ORG_NAME").response("AND Digital").build(),
                            SubmissionQuestion.builder().questionId("APPLICANT_ORG_ADDRESS")
                                    .multiResponse(new String[]{"215 Bothwell Street", "Floor 2", "Glasgow", "Lanarkshire", "G2 7EZ"}).build(),
                            SubmissionQuestion.builder().questionId("APPLICANT_ORG_COMPANIES_HOUSE").response("1234567").build(),
                            SubmissionQuestion.builder().questionId("APPLICANT_ORG_CHARITY_NUMBER").response("22135").build()
                    )))
                    .build();
            final SubmissionSection fundingDetails = SubmissionSection.builder()
                    .sectionId("FUNDING_DETAILS")
                    .sectionStatus(SubmissionSectionStatus.COMPLETED)
                    .questions(new ArrayList<>(List.of(
                            SubmissionQuestion.builder().questionId("APPLICANT_AMOUNT").response("150000").build(),
                            SubmissionQuestion.builder().questionId("BENEFITIARY_LOCATION")
                                    .multiResponse(new String[]{"Scotland", "Wales"}).build()
                    )))
                    .build();
            final SubmissionDefinition definition = SubmissionDefinition.builder()
                    .sections(new ArrayList<>(List.of(
                            SubmissionSection.builder().sectionId("ELIGIBILITY").build(),
                            organisationDetails,
                            fundingDetails
                    )))
                    .build();
            return Submission.builder()
                    .id(submissionId)
                    .definition(definition)
                    .version(2)
                    .status(status)
                    .scheme(scheme)
                    .applicant(applicant)
                    .mandatorySectionsCompleted(true)
                    .build();
        }

        @Test
        void returnsExistingMandatoryQuestion_WhenSubmissionAlreadyOwnsOne() {
            final UUID submissionId = UUID.randomUUID();
            final GrantScheme scheme = GrantScheme.builder().id(10).version(2).build();
            final GrantApplicant applicant = GrantApplicant.builder().id(1L).userId(applicantUserId).build();
            final Submission submission = buildBrokenSiblingSubmission(submissionId, scheme, applicant, SubmissionStatus.IN_PROGRESS);

            final GrantMandatoryQuestions existing = GrantMandatoryQuestions.builder()
                    .id(UUID.randomUUID())
                    .submission(submission)
                    .build();

            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId))
                    .thenReturn(Optional.of(submission));
            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenReturn(Optional.of(existing));

            final GrantMandatoryQuestions result = serviceUnderTest.ensureMandatoryQuestionForSubmission(submissionId, applicantUserId);

            assertThat(result).isEqualTo(existing);
            verify(grantMandatoryQuestionRepository, never()).save(Mockito.any());
        }

        @Test
        void createsPerSubmissionMandatoryQuestion_SeedsOrgButBlanksFunding_WhenNoneExists() {
            final UUID submissionId = UUID.randomUUID();
            final GrantScheme scheme = GrantScheme.builder().id(10).version(2).build();
            final GrantApplicant applicant = GrantApplicant.builder().id(1L).userId(applicantUserId).build();
            final Submission submission = buildBrokenSiblingSubmission(submissionId, scheme, applicant, SubmissionStatus.IN_PROGRESS);

            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId))
                    .thenReturn(Optional.of(submission));
            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenReturn(Optional.empty());
            when(grantMandatoryQuestionRepository.save(Mockito.any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            final GrantMandatoryQuestions result = serviceUnderTest.ensureMandatoryQuestionForSubmission(submissionId, applicantUserId);

            // Organisation details are seeded from the submission's own definition (its source of truth)
            assertThat(result.getOrgType()).isEqualTo(GrantMandatoryQuestionOrgType.LIMITED_COMPANY);
            assertThat(result.getName()).isEqualTo("AND Digital");
            assertThat(result.getAddressLine1()).isEqualTo("215 Bothwell Street");
            assertThat(result.getAddressLine2()).isEqualTo("Floor 2");
            assertThat(result.getCity()).isEqualTo("Glasgow");
            assertThat(result.getCounty()).isEqualTo("Lanarkshire");
            assertThat(result.getPostcode()).isEqualTo("G2 7EZ");
            assertThat(result.getCompaniesHouseNumber()).isEqualTo("1234567");
            assertThat(result.getCharityCommissionNumber()).isEqualTo("22135");

            // Funding is deliberately blanked so the applicant must re-confirm it for this submission
            assertThat(result.getFundingAmount()).isNull();
            assertThat(result.getFundingLocation()).isNull();

            // Linked to this submission, set in progress, with a fresh (null) gapId
            assertThat(result.getSubmission()).isEqualTo(submission);
            assertThat(result.getGrantScheme()).isEqualTo(scheme);
            assertThat(result.getCreatedBy()).isEqualTo(applicant);
            assertThat(result.getStatus()).isEqualTo(GrantMandatoryQuestionStatus.IN_PROGRESS);
            assertThat(result.getGapId()).isNull();

            // The blanked funding is projected into this submission and its funding section is reopened
            verify(grantMandatoryQuestionRepository).save(Mockito.any());
            verify(serviceUnderTest).addMandatoryQuestionsToSubmissionObject(result);
            verify(submissionRepository).save(submission);

            final SubmissionSection fundingSection = submission.getDefinition().getSections().stream()
                    .filter(section -> "FUNDING_DETAILS".equals(section.getSectionId()))
                    .findFirst().orElseThrow();
            assertThat(fundingSection.getSectionStatus()).isEqualTo(SubmissionSectionStatus.IN_PROGRESS);
            assertThat(fundingSection.getQuestions().stream()
                    .filter(question -> "APPLICANT_AMOUNT".equals(question.getQuestionId()))
                    .findFirst().orElseThrow().getResponse()).isNull();

            // Blanking funding clears the submission's cached "mandatory sections completed" flag
            assertThat(submission.getMandatorySectionsCompleted()).isFalse();
        }

        @Test
        void doesNotRePointSharedMandatoryQuestion_WhenHealingSibling() {
            final UUID submissionId = UUID.randomUUID();
            final GrantScheme scheme = GrantScheme.builder().id(10).version(2).build();
            final GrantApplicant applicant = GrantApplicant.builder().id(1L).userId(applicantUserId).build();
            final Submission submission = buildBrokenSiblingSubmission(submissionId, scheme, applicant, SubmissionStatus.IN_PROGRESS);

            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId))
                    .thenReturn(Optional.of(submission));
            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenReturn(Optional.empty());
            when(grantMandatoryQuestionRepository.save(Mockito.any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            serviceUnderTest.ensureMandatoryQuestionForSubmission(submissionId, applicantUserId);

            // Only the freshly created per-submission record is saved - no shared record is fetched or re-pointed.
            verify(grantMandatoryQuestionRepository, never())
                    .findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(Mockito.anyInt(), Mockito.anyString());
        }

        @Test
        void doesNotCreate_WhenSubmissionAlreadySubmitted() {
            final UUID submissionId = UUID.randomUUID();
            final GrantScheme scheme = GrantScheme.builder().id(10).version(2).build();
            final GrantApplicant applicant = GrantApplicant.builder().id(1L).userId(applicantUserId).build();
            final Submission submission = buildBrokenSiblingSubmission(submissionId, scheme, applicant, SubmissionStatus.SUBMITTED);

            final GrantMandatoryQuestions resolved = GrantMandatoryQuestions.builder()
                    .id(UUID.randomUUID())
                    .createdBy(applicant)
                    .build();

            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId))
                    .thenReturn(Optional.of(submission));
            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenReturn(Optional.empty());
            doReturn(resolved).when(serviceUnderTest)
                    .getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submissionId, applicantUserId);

            final GrantMandatoryQuestions result = serviceUnderTest.ensureMandatoryQuestionForSubmission(submissionId, applicantUserId);

            assertThat(result).isEqualTo(resolved);
            verify(grantMandatoryQuestionRepository, never()).save(Mockito.any());
        }

        @Test
        void throwsNotFound_WhenSubmissionDoesNotExistForApplicant() {
            final UUID submissionId = UUID.randomUUID();

            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> serviceUnderTest.ensureMandatoryQuestionForSubmission(submissionId, applicantUserId));

            verify(grantMandatoryQuestionRepository, never()).save(Mockito.any());
        }

        @Test
        void seedsOrgFromMostRecentMandatoryQuestion_WhenSubmissionDefinitionHasNoOrg() {
            final UUID submissionId = UUID.randomUUID();
            final GrantScheme scheme = GrantScheme.builder().id(10).version(2).build();
            final GrantApplicant applicant = GrantApplicant.builder().id(1L).userId(applicantUserId).build();
            final Submission submission = buildNewSubmissionWithoutOrgInDefinition(submissionId, scheme, applicant);

            final GrantMandatoryQuestions source = GrantMandatoryQuestions.builder()
                    .name("AND Digital")
                    .addressLine1("215 Bothwell Street")
                    .addressLine2("Floor 2")
                    .city("Glasgow")
                    .county("Lanarkshire")
                    .postcode("G2 7EZ")
                    .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
                    .companiesHouseNumber("1234567")
                    .charityCommissionNumber("22135")
                    .fundingAmount(BigDecimal.valueOf(150000))
                    .fundingLocation(new GrantMandatoryQuestionFundingLocation[]{
                            GrantMandatoryQuestionFundingLocation.SCOTLAND
                    })
                    .gapId("GAP-LL-20240101-1")
                    .build();

            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId))
                    .thenReturn(Optional.of(submission));
            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenReturn(Optional.empty());
            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(scheme.getId(), applicantUserId))
                    .thenReturn(Optional.of(source));
            when(grantMandatoryQuestionRepository.save(Mockito.any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            final GrantMandatoryQuestions result = serviceUnderTest.ensureMandatoryQuestionForSubmission(submissionId, applicantUserId);

            // Organisation/address details are seeded from the applicant's most recent MQ (the definition had none)
            assertThat(result.getName()).isEqualTo("AND Digital");
            assertThat(result.getAddressLine1()).isEqualTo("215 Bothwell Street");
            assertThat(result.getOrgType()).isEqualTo(GrantMandatoryQuestionOrgType.LIMITED_COMPANY);
            assertThat(result.getCompaniesHouseNumber()).isEqualTo("1234567");
            assertThat(result.getCharityCommissionNumber()).isEqualTo("22135");

            // Funding + gapId are deliberately blanked for the new submission
            assertThat(result.getFundingAmount()).isNull();
            assertThat(result.getFundingLocation()).isNull();
            assertThat(result.getGapId()).isNull();

            assertThat(result.getStatus()).isEqualTo(GrantMandatoryQuestionStatus.IN_PROGRESS);
            assertThat(result.getSubmission()).isEqualTo(submission);
            assertThat(result.getGrantScheme()).isEqualTo(scheme);
            assertThat(result.getCreatedBy()).isEqualTo(applicant);

            verify(grantMandatoryQuestionRepository).save(Mockito.any());
            verify(submissionRepository).save(submission);
            verify(serviceUnderTest).addMandatoryQuestionsToSubmissionObject(result);
        }

        @Test
        void fallsBackToOrgProfile_WhenNoOrgInDefinitionAndNoPreviousMandatoryQuestion() {
            final UUID submissionId = UUID.randomUUID();
            final GrantScheme scheme = GrantScheme.builder().id(10).version(2).build();
            final GrantApplicant applicant = GrantApplicant.builder()
                    .id(1L)
                    .userId(applicantUserId)
                    .organisationProfile(organisationProfile)
                    .build();
            final Submission submission = buildNewSubmissionWithoutOrgInDefinition(submissionId, scheme, applicant);

            final GrantMandatoryQuestions fromProfile = GrantMandatoryQuestions.builder()
                    .name(organisationProfile.getLegalName())
                    .addressLine1(organisationProfile.getAddressLine1())
                    .city(organisationProfile.getTown())
                    .postcode(organisationProfile.getPostcode())
                    .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
                    .build();

            when(submissionRepository.findByIdAndApplicantUserId(submissionId, applicantUserId))
                    .thenReturn(Optional.of(submission));
            when(grantMandatoryQuestionRepository.findBySubmissionId(submissionId))
                    .thenReturn(Optional.empty());
            when(grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(scheme.getId(), applicantUserId))
                    .thenReturn(Optional.empty());
            when(organisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(organisationProfile))
                    .thenReturn(fromProfile);
            when(grantMandatoryQuestionRepository.save(Mockito.any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            final GrantMandatoryQuestions result = serviceUnderTest.ensureMandatoryQuestionForSubmission(submissionId, applicantUserId);

            verify(organisationProfileMapper).mapOrgProfileToGrantMandatoryQuestion(organisationProfile);
            assertThat(result.getName()).isEqualTo(organisationProfile.getLegalName());
            assertThat(result.getOrgType()).isEqualTo(GrantMandatoryQuestionOrgType.LIMITED_COMPANY);
            assertThat(result.getFundingAmount()).isNull();
            assertThat(result.getFundingLocation()).isNull();
            assertThat(result.getSubmission()).isEqualTo(submission);
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

            final LocalDateTime currentDateTime = LocalDateTime.now();
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            final String dateTime = currentDateTime.format(formatter);

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

            //GAP ID Should be GAP-{environment}-{dateTime}-{version}{recordNumber}-{userId}
            assertThat(methodResponse.getGapId()).isEqualTo("GAP-" + "local" + "-" + dateTime + "-22-1");
            assertThat(methodResponse.getGapId()).isEqualTo(grantMandatoryQuestions.getGapId());
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
        void testGenerateNextPageUrlForSkippingCompaniesHouseAndCharityCommissionForIndividual() {
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
        void testGenerateNextPageUrlForSkippingCompaniesHouseAndCharityCommissionForLocalAuthority() {
            final GrantApplicant applicant = GrantApplicant
                    .builder()
                    .userId(applicantUserId)
                    .build();
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions
                    .builder()
                    .id(MANDATORY_QUESTION_ID)
                    .createdBy(applicant)
                    .orgType(GrantMandatoryQuestionOrgType.LOCAL_AUTHORITY)
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
        final GrantScheme grantScheme = GrantScheme.builder().version(2).build();

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
        void doesNothing_IfSchemeIsVersionOne() {
            final GrantScheme scheme = GrantScheme.builder().version(1).build();
            final Submission submission = Submission.builder()
                    .version(1)
                    .scheme(scheme)
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
                    .scheme(grantScheme)
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
                    .scheme(grantScheme)
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
                    .sections(new ArrayList<>(List.of(eligibility, organisationDetails, fundingDetails)))
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .version(2)
                    .scheme(grantScheme)
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

        @Test
        void doesNotPropagateToOtherSubmissionsForScheme_InMultiAppScheme() {
            final GrantApplicant applicant = GrantApplicant.builder().id(1L).build();
            final GrantScheme scheme = GrantScheme.builder().id(10).version(2).build();

            final UUID submission1Id = UUID.randomUUID();
            final SubmissionDefinition definition1 = SubmissionDefinition.builder()
                    .sections(new ArrayList<>(List.of(
                            SubmissionSection.builder().sectionId("ELIGIBILITY").build(),
                            SubmissionSection.builder().sectionId("ORGANISATION_DETAILS").build(),
                            SubmissionSection.builder().sectionId("FUNDING_DETAILS").build()
                    )))
                    .build();
            final Submission submission1 = Submission.builder()
                    .id(submission1Id)
                    .definition(definition1)
                    .scheme(scheme)
                    .build();

            final UUID submission2Id = UUID.randomUUID();
            final SubmissionDefinition definition2 = SubmissionDefinition.builder()
                    .sections(new ArrayList<>(List.of(
                            SubmissionSection.builder().sectionId("ELIGIBILITY").build(),
                            SubmissionSection.builder().sectionId("ORGANISATION_DETAILS").build(),
                            SubmissionSection.builder().sectionId("FUNDING_DETAILS").build()
                    )))
                    .build();
            final Submission submission2 = Submission.builder()
                    .id(submission2Id)
                    .definition(definition2)
                    .scheme(scheme)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .submission(submission1)
                    .createdBy(applicant)
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

            // Only the submission linked to the MQ is rebuilt; there is deliberately no propagation.
            verify(serviceUnderTest, times(1)).buildOrganisationDetailsSubmissionSection(Mockito.any(), Mockito.any());
            verify(serviceUnderTest, times(1)).buildFundingDetailsSubmissionSection(Mockito.any(), Mockito.any());

            // No other submission for the scheme is saved.
            verify(submissionRepository, never()).save(submission2);
        }
    }

    @Nested
    class buildOrganisationDetailsSubmissionSection {

        @Test
        void addsCorrectDetailsForLimitedCompany() {

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
                    .sections(new ArrayList<>(List.of(eligibility, organisationDetails, fundingDetails)))
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
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
                    applicantType,
                    orgName,
                    orgAddress,
                    charityNumber,
                    companiesHouse
            ));
        }

        @Test
        void addsCorrectDetailsForNonLimitedCompany() {

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
                    .sections(new ArrayList<>(List.of(eligibility, organisationDetails, fundingDetails)))
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .orgType(GrantMandatoryQuestionOrgType.NON_LIMITED_COMPANY)
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

            doReturn(orgName)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_NAME.toString(), mandatoryQuestions);

            doReturn(applicantType)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString(), mandatoryQuestions);

            doReturn(orgAddress)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_ADDRESS.toString(), mandatoryQuestions);

            final SubmissionSection orgDetailsSection = serviceUnderTest.buildOrganisationDetailsSubmissionSection(mandatoryQuestions, status);


            assertThat(orgDetailsSection.getSectionId()).isEqualTo(MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID);
            assertThat(orgDetailsSection.getSectionTitle()).isEqualTo(MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_TITLE);
            assertThat(orgDetailsSection.getSectionStatus()).isEqualTo(status);
            assertThat(orgDetailsSection.getQuestions()).containsExactlyElementsOf(List.of(
                    applicantType,
                    orgName,
                    orgAddress
            ));
        }

        @Test
        void addsCorrectDetailsForIndividual() {

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
                    .sections(new ArrayList<>(List.of(eligibility, organisationDetails, fundingDetails)))
                    .build();

            final Submission submission = Submission.builder()
                    .definition(definition)
                    .build();

            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .orgType(GrantMandatoryQuestionOrgType.INDIVIDUAL)
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

            doReturn(orgName)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_NAME.toString(), mandatoryQuestions);

            doReturn(applicantType)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString(), mandatoryQuestions);

            doReturn(orgAddress)
                    .when(serviceUnderTest).mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_ADDRESS.toString(), mandatoryQuestions);

            final SubmissionSection orgDetailsSection = serviceUnderTest.buildOrganisationDetailsSubmissionSection(mandatoryQuestions, status);


            assertThat(orgDetailsSection.getSectionId()).isEqualTo(MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID);
            assertThat(orgDetailsSection.getSectionTitle()).isEqualTo(MandatoryQuestionConstants.ORGANISATION_INDIVIDUAL_DETAILS_SECTION_TITLE);
            assertThat(orgDetailsSection.getSectionStatus()).isEqualTo(status);
            assertThat(orgDetailsSection.getQuestions()).containsExactlyElementsOf(List.of(
                    applicantType,
                    orgName,
                    orgAddress
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
                    .sections(new ArrayList<>(List.of(eligibility, organisationDetails, fundingDetails)))
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