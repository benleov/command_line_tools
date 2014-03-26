package webservicesapi.display;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.layout.EchoLayout;
import org.apache.commons.configuration.AbstractFileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.display.impl.JCursesConsoleScreen;
import webservicesapi.display.impl.JCursesConsoleAppender;
import webservicesapi.display.impl.PlainConsoleAppender;
import webservicesapi.display.impl.PlainScreen;

/**
 * Returns the appropriate screen. If a jcurses interface can run, it will by default.
 * If the unencrypted settings have the property force.console set to true, the standard console will
 * always be used.
 *
 * @author Ben Leov
 */
public class ScreenFactory {

    private static final Logger logger = LoggerFactory.getLogger(ScreenFactory.class);

    private AbstractFileConfiguration clearProperties;

    public ScreenFactory(AbstractFileConfiguration clearProperties) {
        this.clearProperties = clearProperties;
    }

    public Screen getScreen() {

        boolean jcurses = System.console() != null;

        // the force.console setting can only be adhered to if we have a System.console
        // to use
        
        if (jcurses && clearProperties.containsKey("force.console")) {
            jcurses = !clearProperties.getBoolean("force.console");
        }

        if (jcurses) {
            try {

                final JCursesConsoleScreen screen = new JCursesConsoleScreen();
                JCursesConsoleAppender appender = new JCursesConsoleAppender(screen);

                // attach the jcurses appender so it get the logging events
                // which are displayed on the screen

                setAppender(appender);
                return screen;

            } catch (ScreenException e) {
                logger.error("Revering to Console due to error", e);
            }
        }

        setAppender(new PlainConsoleAppender());
        return new PlainScreen();
    }

    private void setAppender(AppenderBase appender) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset(); // we want to override the default-config.

        appender.setContext(lc);
        appender.setLayout(new EchoLayout<ILoggingEvent>());
        appender.start();

        ch.qos.logback.classic.Logger root = lc.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.addAppender(appender);

    }

}
