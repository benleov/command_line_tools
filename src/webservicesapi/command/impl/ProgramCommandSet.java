package webservicesapi.command.impl;

import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.OutputQueue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Launches external applications.
 *
 * @author Ben Leov
 */
public class ProgramCommandSet implements CommandSet {

    private EncryptedProperties configuration;

    public ProgramCommandSet(EncryptedProperties configuration) {

        this.configuration = configuration;
    }

    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            public String[] getRequiredProperties() {
                return null;
            }

            @Override
            public String getCommandName() {
                return "firefox";
            }

            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {
                try {
                    Runtime.getRuntime().exec("firefox www.google.com www.gmail.com www.facebook.com");
                } catch (IOException e) {
                    throw new InvalidCommandException(e);
                }
            }

            @Override
            public String getUsage() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public String getHelp() {
                return "Starts the internet browser firefox";
            }
        });


        return commands;
    }

}
