package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetGrantSchemeDto {
    private long id;
    private long funderId;
    private int version;
    private ZonedDateTime lastUpdated;
    private long lastUpdatedBy;
    private String ggisIdentifier;
    private String schemeName;
    private String schemeContact;
    private String sectionId;
    private String sectionTitle;
    private String sectionStatus;
}
