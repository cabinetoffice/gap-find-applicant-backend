package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrandAdvertDto;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
}