package gov.cabinetoffice.gap.applybackend.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SQSConfig {

    @Bean
    AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.defaultClient();
    }
}
