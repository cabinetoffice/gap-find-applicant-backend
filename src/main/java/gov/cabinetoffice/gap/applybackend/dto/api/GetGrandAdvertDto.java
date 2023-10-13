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
public class GetGrandAdvertDto {
    private UUID id;
    private String externalSubmissionUrl;
    @Builder.Default
    private int version = 1;
    //need jsonProperty because Jackson removes the 'is' from 'isInternal'
    @JsonProperty("isInternal")
    private boolean isInternal;
    private Integer grantApplicationId;
    private Integer grantSchemeId;
    @JsonProperty("isAdvertOnlyInContentful")
    private boolean isAdvertOnlyInContentful;
}
