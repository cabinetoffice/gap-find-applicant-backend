package gov.cabinetoffice.gap.applybackend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/health")
@RestController
public class HealthController {

    @GetMapping
    public ResponseEntity<String> getHealthCheck() {
        return ResponseEntity.ok("Service up");
    }
}
