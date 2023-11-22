package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
public class GetGrantSchemeDto {
    private Integer id;
    private Integer funderId;
    private Integer version;
    private Instant createdDate;
    private Instant lastUpdated;
    private Integer lastUpdatedBy;
    private String ggisIdentifier;
    private String name;
    private String email;
}