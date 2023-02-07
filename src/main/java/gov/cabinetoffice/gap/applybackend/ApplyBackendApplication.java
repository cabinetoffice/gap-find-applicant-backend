package gov.cabinetoffice.gap.applybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ApplyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplyBackendApplication.class, args);
	}

}
