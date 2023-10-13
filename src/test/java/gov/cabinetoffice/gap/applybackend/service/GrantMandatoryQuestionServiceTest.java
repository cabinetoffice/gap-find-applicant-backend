package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GrantMandatoryQuestionServiceTest {

    private final String applicantUserId ="75ab5fbd-0682-4d3d-a467-01c7a447f07c";


    @Mock
    private GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;

    @InjectMocks
    private GrantMandatoryQuestionService serviceUnderTest;

    final ArgumentCaptor<GrantMandatoryQuestions> captor = ArgumentCaptor.forClass(GrantMandatoryQuestions.class);

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
}