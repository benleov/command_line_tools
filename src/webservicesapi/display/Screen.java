package webservicesapi.display;

import webservicesapi.input.CommandParserListener;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ben Leov
 */
public abstract class Screen {
    protected static final String COMMAND_QUIT_QUIT = "quit";
    protected static final String COMMAND_QUIT_EXIT = "exit";
    protected static final String COMMAND_CLEAR_SCREEN = "clear";

    private Set<CommandParserListener> listeners;

    public Screen() {
        listeners = new HashSet<CommandParserListener>();
    }

    /**
     * Displays the screen.
     *
     * @throws ScreenException Thrown if the screen cannot be displayed for
     *                         any reason.
     */
    public abstract void init() throws ScreenException;

    /**
     * Exit screen
     */
    public abstract void finish();

    /**
     *
     */
    public abstract void start();


    protected void notifyListeners(String read) {
        for (CommandParserListener curr : listeners) {
            curr.onLineRead(read);
        }
    }


    public void addListener(CommandParserListener listener) {
        listeners.add(listener);
    }

    public void setCommand(String last) {
        // TODO:
    }
}
