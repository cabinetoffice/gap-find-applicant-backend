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
@Configuration("userServiceConfigurationProperties")
@ConfigurationProperties(prefix = "user-service")
public class UserServiceConfig {
    @NotNull
    private String domain;

    @NotNull
    private String cookieName;
}
