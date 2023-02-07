package gov.cabinetoffice.gap.applybackend.config.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties
public class EnvironmentProperties {

    private String environmentName;
    private String frontEndUri;
}
