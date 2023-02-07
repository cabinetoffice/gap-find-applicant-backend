package gov.cabinetoffice.gap.applybackend.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPIDocs() {
        return new OpenAPI().info(new Info().title("GAP Applicant REST API").description("The REST API for the GAP Applicant Backend").version("v0.1.0"));
    }
}
