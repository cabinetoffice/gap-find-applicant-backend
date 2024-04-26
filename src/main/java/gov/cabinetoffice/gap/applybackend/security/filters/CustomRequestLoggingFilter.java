package gov.cabinetoffice.gap.applybackend.security.filters;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Component
public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {

    public CustomRequestLoggingFilter() {
        this.setIncludeHeaders(true);
        this.setIncludeQueryString(true);
        this.setMaxPayloadLength(10000);
        this.setIncludePayload(true);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        if (request.getRequestURI().endsWith("/health")) {
            return false;
        }

        return logger.isDebugEnabled();
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.debug(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.debug(message);
    }

}
