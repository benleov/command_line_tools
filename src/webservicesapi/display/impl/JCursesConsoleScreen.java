package webservicesapi.display.impl;

import jcurses.event.ActionEvent;
import jcurses.event.ActionListener;
import jcurses.event.ValueChangedEvent;
import jcurses.event.ValueChangedListener;
import jcurses.system.CharColor;
import jcurses.system.Toolkit;
import jcurses.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.display.Screen;
import webservicesapi.display.ScreenException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * JCurses Console Screen implementation.
 *
 * @author Ben Leov
 */
public class JCursesConsoleScreen extends Screen {

    public static int MAX_STATUS_DISPLAY = 500;
    public static int WINDOW_WIDTH;
    public static int WINDOW_HEIGHT;

    private static final Logger logger = LoggerFactory.getLogger(JCursesConsoleScreen.class);

    private static boolean error = false;

    static {

        try {
            WINDOW_HEIGHT = Toolkit.getScreenHeight() - 2;
            WINDOW_WIDTH = Toolkit.getScreenWidth() - 2;
        } catch (UnsatisfiedLinkError e) {

            // this error will be thrown if the jcurses library cannot be
            // loaded

            logger.error("Cannot load jcurses library. This is likely caused by trying to run the 32 bit" +
                    " libjcurses.so on a 64 bit system. Remove ./lib/libjcurses_32.so from the library directory," +
                    " rename libjcurses_64.so to libjcurses.so and restart the application.  ", e);
            error = true;
        }
    }

    public static final int OK_WIDTH = 10;
    public static final int OK_HEIGHT = 3;

    public static final int OUTPUT_WIDTH = WINDOW_WIDTH - 1;
    public static final int OUTPUT_HEIGHT = WINDOW_HEIGHT - 8;

    public static final int STATUS_WIDTH = OUTPUT_WIDTH;
    public static final int STATUS_HEIGHT = 3;

    public static final int COMMAND_WIDTH = OUTPUT_WIDTH;
    public static final int COMMAND_HEIGHT = 3;

    private final TextField command;
    private final TextArea output;
    private final TextField status;
    private Set<ConsoleListener> listeners;
    private Window window;
    private Button ok;
    private java.util.List<String> statusList;

    public JCursesConsoleScreen() throws ScreenException {

        if (error) {
            throw new ScreenException("Curses screen could not be created. See previous errors.");
        }

        command = new TextField(COMMAND_WIDTH);
        output = new TextArea(OUTPUT_WIDTH, OUTPUT_HEIGHT);
        status = new TextField(OUTPUT_WIDTH);

        ok = new Button("ok");
        ok.setShortCut('\n');
        listeners = new HashSet<ConsoleListener>();
        statusList = new java.util.ArrayList<String>();
    }

    @Override
    public void init() throws ScreenException {

        if (error) {
            throw new ScreenException("Curses screen could not be displayed. See previous errors.");
        }

        Toolkit.init();

        new WelcomeScreen().init();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // dont care
        }

//        if (Toolkit.getScreenHeight() < WINDOW_HEIGHT ||
//                Toolkit.getScreenWidth() < WINDOW_WIDTH) {
//
//            Toolkit.shutdown();
//
//            throw new ScreenException("Cannot create console as screen is too small. Please resize your" +
//                    " console window.");
//
//        }

        Toolkit.clearScreen(new CharColor(CharColor.WHITE, CharColor.BLACK));

        window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, true, "Console");

        DefaultLayoutManager manager = new DefaultLayoutManager();
        manager.bindToContainer(window.getRootPanel());

        command.addListener(new ValueChangedListener() {

            public void valueChanged(ValueChangedEvent valueChangedEvent) {

                String text = command.getText();

                if (text.length() > 0) {
                    notifyAllCharTyped(text.charAt(text.length() - 1));
                }
            }
        });

        ok.addListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                notifyAllStringEntered(command.getText());
                command.setText("");
            }
        });

        // add to layout manager

        // addWidget(Widget widget, int x, int y, int width, int height,
        // int verticalConstraint, int horizontalConstraint)

        manager.addWidget(new Label("Please type a command."),
                0, 0, 22, 1,
                WidgetsConstants.ALIGNMENT_LEFT,
                WidgetsConstants.ALIGNMENT_LEFT);

                manager.addWidget(new Label("Type \"help\" to see available commands, or \"quit\" to exit."),
                0, 2, WINDOW_WIDTH - 2, 1,
                WidgetsConstants.ALIGNMENT_RIGHT,
                WidgetsConstants.ALIGNMENT_RIGHT);

        manager.addWidget(command,
                0, 1, COMMAND_WIDTH, COMMAND_HEIGHT,
                WidgetsConstants.ALIGNMENT_LEFT,
                WidgetsConstants.ALIGNMENT_LEFT);


        manager.addWidget(ok,
                0, 1, OK_WIDTH, OK_HEIGHT,
                WidgetsConstants.ALIGNMENT_RIGHT,
                WidgetsConstants.ALIGNMENT_RIGHT);

        manager.addWidget(output,
                0, 5, OUTPUT_WIDTH, OUTPUT_HEIGHT,
                WidgetsConstants.ALIGNMENT_LEFT,
                WidgetsConstants.ALIGNMENT_LEFT);

        manager.addWidget(status,
                0, WINDOW_HEIGHT - 3, STATUS_WIDTH, STATUS_HEIGHT,
                WidgetsConstants.ALIGNMENT_LEFT,
                WidgetsConstants.ALIGNMENT_LEFT);

        window.show();

    }

    @Override
    public void finish() {
        Toolkit.shutdown();
        System.exit(0);
    }

    @Override
    public void start() {

        final Object sync = new Object();

        addListener(new ConsoleListener() {

            public void onKeyTyped(char ch) {
                // TODO: live search
            }

            public void onStringEntered(String text) {

                setStatus("Processing Command...");

                if (text.equalsIgnoreCase(COMMAND_QUIT_QUIT) ||
                        text.equalsIgnoreCase(COMMAND_QUIT_EXIT)) {

                    synchronized (sync) {
                        sync.notify();
                    }

                } else if (text.equals(COMMAND_CLEAR_SCREEN)) {
                    clearStatus();
                } else {
                    notifyListeners(text);
                }
            }
        });

        synchronized (sync) {
            try {
                sync.wait();
            } catch (InterruptedException e) {
                // nothing
            }
        }

    }

    public void clearStatus() {
        statusList.clear();
        WidgetUtilities.invokeAndWait(new Runnable() {
            public void run() {
                JCursesConsoleScreen.this.output.setText("", true);
                window.show();
            }
        });
    }

    public void setCommand(final String command) {

        WidgetUtilities.invokeAndWait(new Runnable() {
            public void run() {
                JCursesConsoleScreen.this.command.setText(command);
                window.show();
            }
        });
    }

    public void setStatus(final String status) {

        WidgetUtilities.invokeAndWait(new Runnable() {
            public void run() {
                JCursesConsoleScreen.this.status.setText(status);
                window.show();
            }
        });
    }

    /**
     * Word wraps the output so it will fit within the window.
     *
     * @param output
     */
    public void appendOutput(String output) {

        int width = WINDOW_WIDTH - 5;

        // split into lines that the window width can display without scrolling

        java.util.List<String> split =  new ArrayList<String>();

        for (int x = 0; x < output.length();) {

            int lineBreak = x + width;

            // the line is bigger than when the line should break

            if (lineBreak < output.length()) {

                int nextNewLine = output.indexOf('\n', x);

                // check if there is a newline before the end of the screen. use that as our line break

                if (nextNewLine != -1 && nextNewLine <= lineBreak) {
                    split.add(output.substring(x, nextNewLine).trim());
                    x = nextNewLine + 1;         // plus one to skip the new line character
                } else {
                    // line is in the middle of string, so must be broken down

                    int last = lineBreak;

                    while (last > x) {

                        // dont break in the middle of a word. work our way back until we find whitespace.

                        if (Character.isWhitespace(output.charAt(last))) {
                            split.add(output.substring(x, last));
                            x = last + 1;
                            break;
                        } else {
                            last--;
                        }
                    }

                }

            } else {
                // the last line
                split.add(output.substring(x, output.length()));
                break;
            }
        }

        statusList.addAll(0, split);

        // trim the list to the correct size

        while (statusList.size() > MAX_STATUS_DISPLAY) {
            statusList.remove(statusList.size() - 1);
            JCursesConsoleScreen.this.output.setText("", true);
        }

        final StringBuilder full = new StringBuilder();

        for (String curr : statusList) {
            full.append(curr);
            full.append("\n");
        }

        WidgetUtilities.invokeAndWait(new Runnable() {
            public void run() {
                JCursesConsoleScreen.this.output.setText(full.toString(), true);
                window.show();
            }
        });
    }

    private void notifyAllCharTyped(char ch) {

        for (ConsoleListener curr : listeners) {
            curr.onKeyTyped(ch);
        }
    }

    private void notifyAllStringEntered(String ch) {

        for (ConsoleListener curr : listeners) {
            curr.onStringEntered(ch);

        }
    }


    /**
     * Adds a listener that will be notified of console events, such as the user entering
     * text.
     *
     * @param listener The listener to be notified.
     */
    private void addListener(ConsoleListener listener) {
        listeners.add(listener);
    }
}