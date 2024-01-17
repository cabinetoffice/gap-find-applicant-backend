package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicationStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
public class GetGrantApplicationDto {
    private Integer id;
    private Integer grantSchemeId;
    private Integer version;
    private Instant created;
    private Instant lastUpdated;
    private Integer lastUpdatedBy;
    private String applicationName;
    private GrantApplicationStatus applicationStatus;
}
