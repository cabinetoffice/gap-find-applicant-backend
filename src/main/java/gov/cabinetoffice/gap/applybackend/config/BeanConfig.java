package gov.cabinetoffice.gap.applybackend.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
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

    private final CognitoConfigProperties cognitoProps;
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
    public AWSCognitoIdentityProvider getCognitoClientBuilder() {
        final BasicAWSCredentials awsCredentials = new BasicAWSCredentials(cognitoProps.getAccessKey(), cognitoProps.getSecretKey());
        return AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(cognitoProps.getRegion())
                .build();
    }

    @Bean
    public NotificationClient notificationClient() {
        return new NotificationClient(notifyProperties.getApiKey());
    }
}
