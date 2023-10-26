package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.GetContentfulAdvertExistsDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantAdvertDto;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantAdvertControllerTest {
    @Mock
    GrantAdvertService grantAdvertService;
    @InjectMocks
    GrantAdvertController grantAdvertController;

    @Nested
    class generateGetGrantAdvertDtoFromAdvertSlug {

        @Test
        void generateGetGrantAdvertDtoFromAdvertSlug_happyPath() {
            final GrantAdvert grantAdvert = GrantAdvert.builder()
                    .build();
            final GetGrantAdvertDto getGrantAdvertDto = GetGrantAdvertDto.builder()
                    .build();

            when(grantAdvertService.getAdvertByContentfulSlug("slug")).thenReturn(grantAdvert);
            when(grantAdvertService.generateGetGrantAdvertDto(grantAdvert)).thenReturn(getGrantAdvertDto);

            final GetGrantAdvertDto result = grantAdvertController.generateGetGrantAdvertDtoFromAdvertSlug("slug").getBody();

            assertThat(result).isEqualTo(getGrantAdvertDto);
        }

        @Test
        void generateGetGrantAdvertDtoFromAdvertSlug_HandlesNotFoundException() {
            final String contentfulSlug = "chargepoint-grant-for-homeowners-1";
            final GetGrantAdvertDto getGrantAdvertDto = GetGrantAdvertDto.builder()
                    .isAdvertInDatabase(false)
                    .build();

            when(grantAdvertService.getAdvertByContentfulSlug(contentfulSlug))
                    .thenThrow(NotFoundException.class);

            final GetGrantAdvertDto result = grantAdvertController.generateGetGrantAdvertDtoFromAdvertSlug(contentfulSlug).getBody();

            assertThat(result).isEqualTo(getGrantAdvertDto);
        }

        @Test
        void generateGetGrantAdvertDtoFromAdvertSlug_ThrowsAnyOtherKindOfException() {
            final String contentfulSlug = "chargepoint-grant-for-homeowners-1";

            when(grantAdvertService.getAdvertByContentfulSlug(contentfulSlug))
                    .thenThrow(IllegalArgumentException.class);

            assertThrows(IllegalArgumentException.class, () -> grantAdvertController.generateGetGrantAdvertDtoFromAdvertSlug(contentfulSlug));
        }
    }

    @Nested
    class advertExistsInContentful {
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

    @Nested

    class generateGetGrantAdvertDtoFromSchemeId {
        @Test
        void generateGetGrantAdvertDtoFromSchemeId() {
            final String schemeId = "25";
            final GrantAdvert grantAdvert = GrantAdvert.builder()
                    .build();
            final GetGrantAdvertDto getGrantAdvertDto = GetGrantAdvertDto.builder()
                    .build();

            when(grantAdvertService.getAdvertBySchemeId(schemeId)).thenReturn(grantAdvert);
            when(grantAdvertService.generateGetGrantAdvertDto(grantAdvert)).thenReturn(getGrantAdvertDto);

            final GetGrantAdvertDto result = grantAdvertController.generateGetGrantAdvertDtoFromSchemeId(schemeId).getBody();

            assertThat(result).isEqualTo(getGrantAdvertDto);
        }

        @Test
        void generateGetGrantAdvertDtoFromSchemeId_ThrowsAnyOtherKindOfException() {
            final String schemeId = "25";

            when(grantAdvertService.getAdvertBySchemeId(schemeId))
                    .thenThrow(IllegalArgumentException.class);

            assertThrows(IllegalArgumentException.class, () -> grantAdvertController.generateGetGrantAdvertDtoFromSchemeId(schemeId));
        }
    }
}