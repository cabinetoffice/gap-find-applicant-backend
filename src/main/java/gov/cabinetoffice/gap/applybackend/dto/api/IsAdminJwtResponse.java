package gov.cabinetoffice.gap.applybackend.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IsAdminJwtResponse {

    //need jsonProperty because Jackson removes the 'is' from 'isValid'
    @JsonProperty("isValid")
    private boolean isValid;
    @JsonProperty("isAdmin")
    private boolean isAdmin;
    @JsonProperty("isApplicant")
    private boolean isApplicant;
}
