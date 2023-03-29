package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtPayload {
    private String sub;
    private String lastLogin;
    private String features;
    private String iss;
    private String cognitoUsername;
    private String givenName;
    private String aud;
    private String eventId;
    private String tokenUse;
    private String phoneNumber;
    private int exp;
    private int iat;
    private String familyName;
    private String email;
    private String isAdmin;
}