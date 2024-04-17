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
@Configuration("lambdaProperties")
@ConfigurationProperties(prefix = "lambda")
public class LambdaSecretConfigProperties {

    private String secret;

    private String privateKey;

}
