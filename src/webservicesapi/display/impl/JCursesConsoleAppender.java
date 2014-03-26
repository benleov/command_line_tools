package webservicesapi.display.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * The console appender uses logback to log the output from the commands.
 *
 * @author Ben Leov
 */
public class JCursesConsoleAppender extends AppenderBase {


    private JCursesConsoleScreen screen;

    public JCursesConsoleAppender(JCursesConsoleScreen screen) {
        this.screen = screen;
    }

    @Override
    protected void append(Object o) {

        if (o instanceof LoggingEvent) {
            LoggingEvent event = (LoggingEvent) o;

            if (event.getThrowableProxy() != null) {
                screen.appendOutput("[ERROR MESSAGE] " + event.getThrowableProxy().getMessage());
            } else {
                // only log info
                if (event.getLevel().isGreaterOrEqual(Level.INFO)) {
                    screen.appendOutput(o.toString());
                }
            }

        } else {
            screen.appendOutput(o.toString());
        }
    }
}
