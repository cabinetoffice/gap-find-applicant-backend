package gov.cabinetoffice.gap.applybackend.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateAttachmentDto {
    private String uri;
    @JsonProperty("isClean")
    private Boolean isClean;
}
