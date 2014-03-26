package webservicesapi.command.impl;

import com.skype.Skype;
import com.skype.SkypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * Unimplemented.
 *
 * Controls skype.
 * 
 * @author Ben Leov
 */
public class SkypeCommandSet implements CommandSet {

    // TODO: copy files to tmp from lib folder

    ///tmp/libJSA.so: libdbus-1.so.0:

    private Logger logger = LoggerFactory.getLogger(SkypeCommandSet.class);

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return null;
            }

            @Override
            public String getCommandName()  {
                return "skype";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {
                try {

                    System.out.println("Running: " + Skype.isRunning());
                    System.out.println("Running: " + Skype.chat("blah blah"));
//                    System.out.println("Running: " + Skype.setDeamon(true));
//                    System.out.println("Running: " + Skype.isRunning());
                } catch (SkypeException e) {
                    throw new InvalidCommandException(e);
                }

            }

            @Override
            public String getUsage() {
                return null;
            }

            public String getHelp() {
                return "Provides skype functions";
            }
        });
        return commands;
    }
}
