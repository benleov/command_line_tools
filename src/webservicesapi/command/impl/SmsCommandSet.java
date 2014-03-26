package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * Note: Unimplemented.
 *
 * Provides the sending of SMS via the pennytel sms api.
 *
 * @author Ben Leov
 */
public class SmsCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(SmsCommandSet.class);

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
                return "sms";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getUsage() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getHelp() {
                return "Sends an sms";  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        return commands;
    }
}
