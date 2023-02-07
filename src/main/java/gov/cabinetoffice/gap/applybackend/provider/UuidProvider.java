package gov.cabinetoffice.gap.applybackend.provider;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidProvider {

    public UUID uuid() {
        return UUID.randomUUID();
    }
}
