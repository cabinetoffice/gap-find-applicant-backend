package gov.cabinetoffice.gap.applybackend.utils;

import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityContextHelper {
    public static UUID getUserIdFromSecurityContext() {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UUID.fromString(jwtPayload.getSub());
    }
}
