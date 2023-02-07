package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class GetNavigationParamsDto {

    private boolean responseAccepted;

    @Builder.Default
    public Map<String, Object> nextNavigation = new HashMap<>();
}
