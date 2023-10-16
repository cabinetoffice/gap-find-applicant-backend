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
@Configuration("contentfulConfigurationProperties")
@ConfigurationProperties(prefix = "contentful")
public class ContentfulConfigProperties {

    private String spaceId;

    private String environmentId;

    private String accessToken;

    private String deliveryAPIAccessToken;

}
