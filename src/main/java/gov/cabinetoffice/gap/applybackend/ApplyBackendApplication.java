package gov.cabinetoffice.gap.applybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ComponentScan(basePackages = "gov.cabinetoffice.gap")
public class ApplyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplyBackendApplication.class, args);
    }

}
