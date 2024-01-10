package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicationStatus;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GrantApplicationControllerTest {

    @Mock
    private GrantApplicationService grantApplicationService;

    @InjectMocks
    private GrantApplicationController controllerUnderTest;

    @Test
    void getApplicationStatusFromSchemeId_returnsApplicationStatus() {
        GrantApplication mockGrantApplication = GrantApplication.builder().id(1)
                .applicationStatus(GrantApplicationStatus.REMOVED).build();
        when(grantApplicationService.getGrantApplicationByGrantScheme(1)).thenReturn(mockGrantApplication);
        String expected = GrantApplicationStatus.REMOVED.toString();

        assertEquals(ResponseEntity.ok(expected),
                controllerUnderTest.getApplicationStatusFromSchemeId(1));
    }

    @Test
    void getApplicationStatusFromSchemeId_returnsExternalApplicationWhenNoApplication() {
        when(grantApplicationService.getGrantApplicationByGrantScheme(1)).thenReturn(null);

        assertEquals(ResponseEntity.ok("EXTERNAL_APPLICATION"),
                controllerUnderTest.getApplicationStatusFromSchemeId(1));
    }
}
