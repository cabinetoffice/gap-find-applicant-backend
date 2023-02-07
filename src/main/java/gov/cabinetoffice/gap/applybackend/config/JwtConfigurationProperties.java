package gov.cabinetoffice.gap.applybackend.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Configuration("jwtConfigurationProperties")
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigurationProperties {

    private String normalisedJWT;
    private String rawJWT;
    private String sub;
    private String givenName;
    private String familyName;

}