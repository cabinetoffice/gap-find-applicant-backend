package gov.cabinetoffice.gap.applybackend.config;

import com.contentful.java.cda.CDAClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ContentfulConfig {

    private final ContentfulConfigProperties configProperties;

    @Bean
    public CDAClient getContentfulDeliveryClient() {
        return CDAClient.builder()
                .setToken(configProperties.getDeliveryAPIAccessToken())
                .setSpace(configProperties.getSpaceId())
                .setEnvironment(configProperties.getEnvironmentId())
                .build();
    }

}
