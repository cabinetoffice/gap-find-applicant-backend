package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.model.RegisterApplicant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterApplicantDto {
    private String message;
    private RegisterApplicant registerApplicantResponse;
}


