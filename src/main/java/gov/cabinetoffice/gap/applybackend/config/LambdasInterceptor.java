package gov.cabinetoffice.gap.applybackend.config;

import gov.cabinetoffice.gap.applybackend.security.interceptors.AuthorizationHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class LambdasInterceptor implements WebMvcConfigurer {

    private static final String UUID_REGEX_STRING = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
    private final LambdaSecretConfigProperties lambdaSecretConfigProperties;

    @Bean(name="lambdas_applicant_interceptor")
    AuthorizationHeaderInterceptor lambdasInterceptor() {
        return new AuthorizationHeaderInterceptor(lambdaSecretConfigProperties.getSecret(),
                lambdaSecretConfigProperties.getPrivateKey());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(lambdasInterceptor())
                .addPathPatterns( "/submissions/{submissionId:" + UUID_REGEX_STRING
                        + "}/question/{questionId:" + UUID_REGEX_STRING + "}/attachment/scanresult")
                .order(Ordered.HIGHEST_PRECEDENCE);
    }

}
