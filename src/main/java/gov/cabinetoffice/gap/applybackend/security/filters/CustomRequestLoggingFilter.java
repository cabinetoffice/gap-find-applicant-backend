package gov.cabinetoffice.gap.applybackend.security.filters;

import org.springframework.web.filter.AbstractRequestLoggingFilter;
import javax.servlet.http.HttpServletRequest;

public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        if (!request.getRequestURI().endsWith("/health")) {
            logger.debug(message);
        }
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        if (!request.getRequestURI().endsWith("/health")) {
            logger.debug(message);
        }
    }
}
