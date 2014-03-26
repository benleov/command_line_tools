package webservicesapi.display.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.display.Screen;
import webservicesapi.display.ScreenException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Standard console screen.
 */
public class PlainScreen extends Screen {
    private static Logger logger = LoggerFactory.getLogger(PlainScreen.class);

    @Override
    public void init() throws ScreenException {
        logger.info("Awaiting Input. Type Quit[ENTER] to quit.");
    }

    @Override
    public void finish() {
        logger.info("Shutting down.");
    }

    @Override
    public void start() {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(reader);

        String read = null;

        do {

            if (read != null) {
                notifyListeners(read);
            }

            try {
                read = buffer.readLine();      // blocks here
            } catch (IOException e) {
                logger.error("Parser IO Exception", e);
            }

            if (read.equals("kill")) {
                logger.info("Forced Shutdown.");
                System.exit(0);
            }

        } while (!read.equalsIgnoreCase(COMMAND_QUIT_QUIT) && !read.equalsIgnoreCase(COMMAND_QUIT_EXIT));
    }
}
