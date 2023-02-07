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
@Configuration("s3ConfigurationProperties")
@ConfigurationProperties(prefix = "aws")
public class S3ConfigProperties {

    @NotNull
    private String bucket;
}
