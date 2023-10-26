package gov.cabinetoffice.gap.applybackend.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetGrantAdvertDto {

    private UUID id;

    private String externalSubmissionUrl;

    private int version;

    @JsonProperty("isInternal")
    private boolean isInternal;

    private Integer grantApplicationId;

    private Integer grantSchemeId;

    @JsonProperty("isAdvertInDatabase")
    private boolean isAdvertInDatabase;
}
