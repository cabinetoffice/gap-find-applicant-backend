package gov.cabinetoffice.gap.applybackend.testingextensions;

import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.LoggerFactory;

public class InfoLogCaptureExtension implements ParameterResolver, AfterTestExecutionCallback {

    private final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    private InfoLogCapture logCapture;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == InfoLogCapture.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        logCapture = new InfoLogCapture();

        setup();

        return logCapture;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        teardown();
    }

    private void setup() {
        logger.addAppender(logCapture.getListAppender());
        logCapture.start();
    }

    private void teardown() {
        if (logCapture == null || logger == null) {
            return;
        }

        logger.detachAndStopAllAppenders();
        logCapture.stop();
    }

}
