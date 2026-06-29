package gov.cabinetoffice.gap.applybackend.service.backfill;

import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionStatus;
import gov.cabinetoffice.gap.applybackend.model.DiligenceCheck;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantBeneficiary;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import gov.cabinetoffice.gap.applybackend.model.SubmissionDefinition;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestion;
import gov.cabinetoffice.gap.applybackend.model.SubmissionSection;
import gov.cabinetoffice.gap.applybackend.repository.DiligenceCheckRepository;
import gov.cabinetoffice.gap.applybackend.repository.GrantBeneficiaryRepository;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import gov.cabinetoffice.gap.applybackend.repository.SubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MandatoryQuestionBackfillRunnerTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private DiligenceCheckRepository diligenceCheckRepository;

    @Mock
    private GrantBeneficiaryRepository grantBeneficiaryRepository;

    @Mock
    private GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;

    @InjectMocks
    private MandatoryQuestionBackfillRunner runner;

    private Submission buildSubmission(final UUID id, final String orgTypeResponse) {
        final List<SubmissionQuestion> questions = new ArrayList<>();
        if (orgTypeResponse != null) {
            questions.add(SubmissionQuestion.builder()
                    .questionId("APPLICANT_TYPE")
                    .response(orgTypeResponse)
                    .build());
        }
        final SubmissionDefinition definition = SubmissionDefinition.builder()
                .sections(List.of(SubmissionSection.builder()
                        .sectionId("ESSENTIAL")
                        .questions(questions)
                        .build()))
                .build();

        return Submission.builder()
                .id(id)
                .gapId("GAP-PRD-202606100918-256816-86889")
                .scheme(GrantScheme.builder().id(1).build())
                .applicant(GrantApplicant.builder().id(99L).build())
                .definition(definition)
                .build();
    }

    @Test
    void createsFullyPopulatedRecord_WhenAllSourceDataIsAvailable() {
        final UUID submissionId = UUID.randomUUID();
        final Submission submission = buildSubmission(submissionId, "Limited company");

        when(submissionRepository.findSubmittedMultiSubmissionWithoutMandatoryQuestions())
                .thenReturn(List.of(submission));
        when(diligenceCheckRepository.findBySubmissionId(submissionId))
                .thenReturn(Optional.of(DiligenceCheck.builder()
                        .organisationName("AND Digital")
                        .addressStreet("215 Bothwell Street")
                        .addressTown("Glasgow")
                        .addressPostcode("G2 7EZ")
                        .companiesHouseNumber("1234567")
                        .charityNumber("22135")
                        .applicationAmount("500")
                        .build()));
        when(grantBeneficiaryRepository.findBySubmissionId(submissionId))
                .thenReturn(Optional.of(GrantBeneficiary.builder()
                        .locationSco(true)
                        .build()));

        runner.run(null);

        final ArgumentCaptor<GrantMandatoryQuestions> captor = ArgumentCaptor.forClass(GrantMandatoryQuestions.class);
        verify(grantMandatoryQuestionRepository).save(captor.capture());

        final GrantMandatoryQuestions saved = captor.getValue();
        assertThat(saved.getSubmission()).isEqualTo(submission);
        assertThat(saved.getGapId()).isEqualTo(submission.getGapId());
        assertThat(saved.getStatus()).isEqualTo(GrantMandatoryQuestionStatus.COMPLETED);
        assertThat(saved.getName()).isEqualTo("AND Digital");
        assertThat(saved.getFundingAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(saved.getFundingLocation()).containsExactly(GrantMandatoryQuestionFundingLocation.SCOTLAND);
        assertThat(saved.getOrgType()).isEqualTo(GrantMandatoryQuestionOrgType.LIMITED_COMPANY);
    }

    @Test
    void createsPartialRecord_WhenNoSourceDataIsAvailable() {
        final UUID submissionId = UUID.randomUUID();
        final Submission submission = buildSubmission(submissionId, null);

        when(submissionRepository.findSubmittedMultiSubmissionWithoutMandatoryQuestions())
                .thenReturn(List.of(submission));
        when(diligenceCheckRepository.findBySubmissionId(submissionId)).thenReturn(Optional.empty());
        when(grantBeneficiaryRepository.findBySubmissionId(submissionId)).thenReturn(Optional.empty());

        runner.run(null);

        final ArgumentCaptor<GrantMandatoryQuestions> captor = ArgumentCaptor.forClass(GrantMandatoryQuestions.class);
        verify(grantMandatoryQuestionRepository).save(captor.capture());

        final GrantMandatoryQuestions saved = captor.getValue();
        // The record is still created so the one-to-one relationship exists, but with empty values
        assertThat(saved.getSubmission()).isEqualTo(submission);
        assertThat(saved.getStatus()).isEqualTo(GrantMandatoryQuestionStatus.COMPLETED);
        assertThat(saved.getName()).isNull();
        assertThat(saved.getFundingAmount()).isNull();
        assertThat(saved.getFundingLocation()).isNull();
        assertThat(saved.getOrgType()).isNull();
    }

    @Test
    void leavesFundingAmountEmpty_WhenAmountCannotBeParsed() {
        final UUID submissionId = UUID.randomUUID();
        final Submission submission = buildSubmission(submissionId, "Limited company");

        when(submissionRepository.findSubmittedMultiSubmissionWithoutMandatoryQuestions())
                .thenReturn(List.of(submission));
        when(diligenceCheckRepository.findBySubmissionId(submissionId))
                .thenReturn(Optional.of(DiligenceCheck.builder().applicationAmount("not-a-number").build()));
        when(grantBeneficiaryRepository.findBySubmissionId(submissionId)).thenReturn(Optional.empty());

        runner.run(null);

        final ArgumentCaptor<GrantMandatoryQuestions> captor = ArgumentCaptor.forClass(GrantMandatoryQuestions.class);
        verify(grantMandatoryQuestionRepository).save(captor.capture());

        assertThat(captor.getValue().getFundingAmount()).isNull();
    }

    @Test
    void doesNothing_WhenThereAreNoOrphanedSubmissions() {
        when(submissionRepository.findSubmittedMultiSubmissionWithoutMandatoryQuestions())
                .thenReturn(List.of());

        runner.run(null);

        verify(grantMandatoryQuestionRepository, never()).save(any());
    }
}
