package webservicesapi.command.impl;

import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.impl.dict.DictConnection;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Searches an online dictionary.
 * 
 * @author Ben Leov
 */
public class DictionaryCommandSet implements CommandSet {

    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return null;
            }

            @Override
            public String getCommandName()  {
                return "dictionary";
            }

            @Override
            public void processCommand(String command, String parameter,
                                       OutputQueue queue) throws CommandErrorException {

                Output out = new Output(this);
                DictConnection dictionary = new DictConnection();

                try {
                    out.addLine(dictionary.define(parameter));
                } catch (IOException e) {
                    throw new CommandErrorException(e);
                }

                queue.send(out);

            }

            @Override
            public String getUsage() {
                return "<word>";
            }

            @Override
            public String getHelp() {
                return "Queries the dictionary server";
            }
        });
        return commands;
    }
}
