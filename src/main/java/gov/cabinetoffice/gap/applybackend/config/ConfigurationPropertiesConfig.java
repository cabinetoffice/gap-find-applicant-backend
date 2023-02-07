package gov.cabinetoffice.gap.applybackend.config;

import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EnvironmentProperties.class)
public class ConfigurationPropertiesConfig {
}
