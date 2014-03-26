package webservicesapi.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.display.Screen;
import webservicesapi.display.ScreenException;
import webservicesapi.display.ScreenFactory;

/**
 * Parses commands typed into the command line, and notified any listeners.
 *
 * @author Ben Leov
 */
public class CommandParser {

    private static Logger logger = LoggerFactory.getLogger(CommandParser.class);

    private Screen screen;

    public CommandParser(Screen screen) {
        this.screen = screen;
    }

    public void start() throws ScreenException {

        screen.init();
        screen.start();

        logger.debug("Command parser shutting down.");

        screen.finish();
    }

    public void addListener(CommandParserListener tracker) {
        screen.addListener(tracker);
    }

    public Screen getScreen() {
        return screen;
    }
}