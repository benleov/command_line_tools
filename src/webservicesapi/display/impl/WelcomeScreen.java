package webservicesapi.display.impl;

import jcurses.system.CharColor;
import jcurses.system.Toolkit;

/**
 * Displays the welcome frame when the jcurses interface is loadign.
 *
 * @author Ben Leov
 */
public class WelcomeScreen {

    public void init() {

        Toolkit.clearScreen(new CharColor(CharColor.BLACK, CharColor.YELLOW));

        Toolkit.printString("\n  WebServicesAPI V1.0  \n", 28, 10,
               new CharColor(CharColor.CYAN, CharColor.BLACK, CharColor.NORMAL, CharColor.BOLD));
    }
}
