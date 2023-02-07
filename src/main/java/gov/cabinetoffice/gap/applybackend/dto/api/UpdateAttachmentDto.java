package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.enums.GrantAttachmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateAttachmentDto {
    private String uri;
    private Boolean isClean;
}
