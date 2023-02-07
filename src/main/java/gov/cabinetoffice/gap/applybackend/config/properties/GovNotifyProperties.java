package gov.cabinetoffice.gap.applybackend.config.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Configuration("govNotifyConfigurationProperties")
@ConfigurationProperties(prefix = "gov-notify")
public class GovNotifyProperties {

    @NotNull
    private String apiKey;

    @NotNull
    private String submissionConfirmationTemplate;
}
