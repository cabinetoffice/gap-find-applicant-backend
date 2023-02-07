package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateGrantBeneficiaryDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantBeneficiaryDto;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.generators.GetGrantBeneficiaryDTOGenerator;
import gov.cabinetoffice.gap.applybackend.generators.GrantBeneficiaryGenerator;
import gov.cabinetoffice.gap.applybackend.model.GrantBeneficiary;
import gov.cabinetoffice.gap.applybackend.service.GrantBeneficiaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static gov.cabinetoffice.gap.applybackend.generators.CreateGrantBeneficiaryDtoGenerator.generateRandomGrantBeneficiaryDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantBeneficiaryControllerTest {
    @Mock
    private GrantBeneficiaryService grantBeneficiaryService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private GrantBeneficiaryController controllerUnderTest;

    @Test
    void submitResponse_noGrantBeneficiaryId() {
        final CreateGrantBeneficiaryDto createGrantBeneficiaryDto = generateRandomGrantBeneficiaryDTO().build();
        final UUID grantBeneficiaryId = UUID.fromString("1c2eabf0-b33c-433a-b00f-e73d8efca929");

        when(grantBeneficiaryService.addResponse(createGrantBeneficiaryDto, null))
                .thenReturn(grantBeneficiaryId);

        ResponseEntity<UUID> response = controllerUnderTest.submitResponse(createGrantBeneficiaryDto, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grantBeneficiaryId, response.getBody());
    }

    @Test
    void submitResponse_withGrantBeneficiaryId() {
        final CreateGrantBeneficiaryDto createGrantBeneficiaryDto = generateRandomGrantBeneficiaryDTO().build();
        final UUID grantBeneficiaryId = UUID.fromString("1c2eabf0-b33c-433a-b00f-e73d8efca929");

        when(grantBeneficiaryService.addResponse(createGrantBeneficiaryDto, grantBeneficiaryId))
                .thenReturn(grantBeneficiaryId);

        ResponseEntity<UUID> response = controllerUnderTest.submitResponse(createGrantBeneficiaryDto, grantBeneficiaryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grantBeneficiaryId, response.getBody());
    }

    @Test
    void getGrantBeneficiary_successfullyGettingGrantBeneficiary() {

        UUID beneficiaryUUID = UUID.randomUUID();
        GrantBeneficiary testBeneficiary =
                GrantBeneficiaryGenerator.generateRandomGrantBeneficiary().grantBeneficiaryId(beneficiaryUUID).build();
        GetGrantBeneficiaryDto testGetBeneficiaryDTO = GetGrantBeneficiaryDTOGenerator.generateRandomGetGrantBeneficiaryDTO().build();

        when(grantBeneficiaryService.getGrantBeneficiary(beneficiaryUUID))
                .thenReturn(testBeneficiary);
        when(modelMapper.map(testBeneficiary, GetGrantBeneficiaryDto.class))
                .thenReturn(testGetBeneficiaryDTO);

        ResponseEntity<GetGrantBeneficiaryDto> response = controllerUnderTest.getGrantBeneficiary(beneficiaryUUID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testGetBeneficiaryDTO, response.getBody());
    }

    @Test
    void getGrantBeneficiary_unsuccessfulNotFound() {

        UUID beneficiaryUUID = UUID.randomUUID();

        when(grantBeneficiaryService.getGrantBeneficiary(beneficiaryUUID))
                .thenThrow(new NotFoundException("Error Message"));

        assertThrows(NotFoundException.class, () -> controllerUnderTest.getGrantBeneficiary(beneficiaryUUID));
    }
}
