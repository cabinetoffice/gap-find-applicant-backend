package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtPayloadV2 {
    private String sub;
    private String roles;
    private String iss;
    private String aud;
    private int exp;
    private int iat;
    private String email;
}