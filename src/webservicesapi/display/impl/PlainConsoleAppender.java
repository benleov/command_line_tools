package webservicesapi.display.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Appender for the standard console screen.
 */
public class PlainConsoleAppender extends AppenderBase {

    @Override
    protected void append(Object o) {

        if (o instanceof LoggingEvent) {
            LoggingEvent event = (LoggingEvent) o;

            if (event.getThrowableProxy() != null) {
                System.out.println("[ERROR MESSAGE] " + event.getThrowableProxy().getMessage());
            } else {

                if (event.getLevel().isGreaterOrEqual(Level.INFO)) {
                    System.out.println(o.toString());
                }

            }

        } else {
            System.out.println(o.toString());
        }

    }
}
