package gov.cabinetoffice.gap.applybackend.config;

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
@Configuration("cognitoConfigurationProperties")
@ConfigurationProperties(prefix = "cognito")
public class CognitoConfigProperties {

    @NotNull
    private String secretKey;

    @NotNull
    private String accessKey;

    @NotNull
    private String region;

    @NotNull
    private String userPoolId;

    @NotNull
    private String userPassword;

    @NotNull
    private String domain;

    @NotNull
    private String appClientId;

}
