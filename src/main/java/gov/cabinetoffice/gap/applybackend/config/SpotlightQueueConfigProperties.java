package gov.cabinetoffice.gap.applybackend.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "spotlight-queue")
public class SpotlightQueueConfigProperties {

    @NotNull
    private String spotlightQueue;

}
