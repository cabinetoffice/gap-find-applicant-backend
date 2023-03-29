package gov.cabinetoffice.gap.applybackend.config;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import gov.cabinetoffice.gap.applybackend.config.properties.GovNotifyProperties;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import uk.gov.service.notify.NotificationClient;

import java.time.Clock;

@RequiredArgsConstructor
@Configuration
public class BeanConfig {

    private final GovNotifyProperties notifyProperties;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        return modelMapper;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplateBuilder()
                .requestFactory(HttpComponentsClientHttpRequestFactory.class) // The standard HTTP library doesn't support PATCH requests but Salesforce requires these to update entities.
                .build();
    }

    @Bean
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public PhoneNumberUtil getPhoneNumberUtil() {
        return PhoneNumberUtil.getInstance();
    }

    @Bean
    public NotificationClient notificationClient() {
        return new NotificationClient(notifyProperties.getApiKey());
    }
}
