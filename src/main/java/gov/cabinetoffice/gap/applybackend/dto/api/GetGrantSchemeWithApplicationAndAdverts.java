package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetGrantSchemeWithApplicationAndAdverts {
    private GetGrantSchemeDto grantScheme;
    private GetGrantApplicationDto grantApplication;
    private List<GetGrantAdvertDto> grantAdverts;
}
