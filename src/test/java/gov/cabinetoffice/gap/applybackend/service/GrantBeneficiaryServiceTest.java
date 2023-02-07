package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateGrantBeneficiaryDto;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantBeneficiary;
import gov.cabinetoffice.gap.applybackend.repository.GrantBeneficiaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static gov.cabinetoffice.gap.applybackend.generators.CreateGrantBeneficiaryDtoGenerator.generateRandomGrantBeneficiaryDTO;
import static gov.cabinetoffice.gap.applybackend.generators.GrantBeneficiaryGenerator.generateRandomGrantBeneficiary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantBeneficiaryServiceTest {

    @Mock
    private GrantBeneficiaryRepository grantBeneficiaryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GrantBeneficiaryService serviceUnderTest;

    @Test
    void addResponse_Success() {
        final CreateGrantBeneficiaryDto createGrantBeneficiaryDto = generateRandomGrantBeneficiaryDTO()
                .build();
        final UUID grantBeneficiaryId = UUID.fromString("15ab5fbd-0682-4d3d-a467-01c7a447f07c");
        final GrantBeneficiary grantBeneficiary = generateRandomGrantBeneficiary().build();

        when(grantBeneficiaryRepository.save(any())).thenReturn(generateRandomGrantBeneficiary().build());
        when(grantBeneficiaryRepository.findById(grantBeneficiaryId)).thenReturn(Optional.of(grantBeneficiary));
        when(modelMapper.getConfiguration()).thenReturn(new ModelMapper().getConfiguration());

        final UUID response = serviceUnderTest.addResponse(createGrantBeneficiaryDto, grantBeneficiaryId);

        verify(modelMapper).map(createGrantBeneficiaryDto, grantBeneficiary);
        assertEquals(UUID.fromString("55ab5fbd-0682-4d3d-a467-01c7a447f07c"), response);
    }

    @Test
    void addResponse_GrantBeneficiaryIdDoesNotExist() {
        final CreateGrantBeneficiaryDto createGrantBeneficiaryDto = generateRandomGrantBeneficiaryDTO()
                .build();

        final Exception result = assertThrows(NotFoundException.class, () -> serviceUnderTest.addResponse(createGrantBeneficiaryDto, UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c")));
        assertEquals("Could not find a grant beneficiary with the id: 75ab5fbd-0682-4d3d-a467-01c7a447f07c", result.getMessage());
    }
    
    @Test
    void getGrantBeneficiary_Success_foundBeneficiaryWithMatchingId() {
        final GrantBeneficiary grantBeneficiary = generateRandomGrantBeneficiary().build();
        final UUID grantBeneficiaryId = grantBeneficiary.getGrantBeneficiaryId();

        when(grantBeneficiaryRepository.findBySubmissionId(grantBeneficiaryId)).thenReturn(Optional.of(grantBeneficiary));

        final GrantBeneficiary response = serviceUnderTest.getGrantBeneficiary(grantBeneficiaryId);

        verify(grantBeneficiaryRepository).findBySubmissionId(grantBeneficiaryId);
        assertEquals(grantBeneficiary, response);
    }
    
    @Test
    void getGrantBeneficiary_Unsuccessful_noBeneficiaryFoundWithMatchingId() {
        final UUID submissionId = UUID.randomUUID();

        when(grantBeneficiaryRepository.findBySubmissionId(submissionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serviceUnderTest.getGrantBeneficiary(submissionId));
        verify(grantBeneficiaryRepository).findBySubmissionId(submissionId);
    }
}