package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
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

    public GetGrantSchemeDto(final GrantScheme grantScheme) {
        this.id = grantScheme.getId();
        this.funderId = grantScheme.getFunderId();
        this.version = grantScheme.getVersion();
        this.createdDate = grantScheme.getCreatedDate();
        this.lastUpdated = grantScheme.getLastUpdated();
        this.lastUpdatedBy = grantScheme.getLastUpdatedBy();
        this.ggisIdentifier = grantScheme.getGgisIdentifier();
        this.name = grantScheme.getName();
        this.email = grantScheme.getEmail();
    }
}