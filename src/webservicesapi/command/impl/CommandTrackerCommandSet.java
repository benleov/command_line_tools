package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.display.Screen;
import webservicesapi.input.CommandTracker;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ben Leov
 *         <p/>
 *         TODO: tracks commands. This requires the jcurses console to detect keystrokes (ie the up arrow)
 *         so the user can scroll thru previous commands, or type "last 2" and get the second to last command.
 */
public class CommandTrackerCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(CommandTrackerCommandSet.class);

    private CommandTracker tracker;
    private Screen screen;

    public CommandTrackerCommandSet(CommandTracker tracker, Screen screen) {
        this.tracker = tracker;
        this.screen = screen;
    }

    @Override
    public Set<Command> getCommands() {
        Set<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            @Override
            public String getCommandName()  {
                return "last";
            }
            
            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                int index = 0;

                if (parameter != null) {
                    try {
                        index = Integer.parseInt(parameter);
                    } catch (NumberFormatException e) {
                        logger.warn("Expecting number as parameter. Defaulting to last command.");
                    }
                }

                String last = tracker.getCommand(index);

                if (last != null) {
                    screen.setCommand(last);
                } else {
                    logger.warn("No command found at specified index: " + index);
                }


            }

            @Override
            public String getUsage() {
                return "<index>";
            }

            public String getHelp() {
                return "Tracks the users command history";
            }
        });

        return commands;
    }
}
