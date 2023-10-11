package gov.cabinetoffice.gap.applybackend.utils;

import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {
    public static String getUserIdFromSecurityContext() {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwtPayload.getSub();
    }
}
