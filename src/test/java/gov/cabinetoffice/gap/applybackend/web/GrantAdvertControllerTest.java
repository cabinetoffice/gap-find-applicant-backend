package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.GetContentfulAdvertExistsDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrandAdvertDto;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GrantAdvertControllerTest {
    @Mock
    GrantAdvertService grantAdvertService;
    @InjectMocks
    GrantAdvertController grantAdvertController;

    @Test
    void generateGetGrantAdvertDtoFromAdvertSlug() {
        final GetGrandAdvertDto getGrandAdvertDto = GetGrandAdvertDto.builder()
                .build();

        when(grantAdvertService.getAdvertByContentfulSlug("slug")).thenReturn(getGrandAdvertDto);

        final GetGrandAdvertDto result = grantAdvertController.generateGetGrantAdvertDtoFromAdvertSlug("slug").getBody();

        assertThat(result).isEqualTo(getGrandAdvertDto);
    }

    @Test
    void advertExistsInContentful_ReturnsExpectedData() {

        final String advertSlug = "chargepoint-grant-for-homeowners-1";

        when(grantAdvertService.advertExistsInContentful(advertSlug))
                .thenReturn(true);

        final ResponseEntity<GetContentfulAdvertExistsDto> methodResponse = grantAdvertController.advertExistsInContentful(advertSlug);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        final GetContentfulAdvertExistsDto responseBody = methodResponse.getBody();
        assertThat(responseBody.isAdvertInContentful()).isTrue();
    }
}