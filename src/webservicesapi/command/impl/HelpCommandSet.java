package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.CommandSetRegister;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * Displays help for all implemented commands.
 *
 * @author Ben Leov
 */
public class HelpCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(HelpCommandSet.class);
    private CommandSetRegister register;

    public HelpCommandSet(CommandSetRegister register) {
        this.register = register;
    }

    public Set<Command> getCommands() {
        Set<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            /**
             * Displays a message if the user types nothing and presses enter.
             * @return
             */
            @Override
            public String getCommandName() {
                return "";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {
                Output out = new Output(this);
                out.addLine("Type \"help\" for information about available commands.");
                queue.send(out);
            }

            @Override
            public String getUsage() {
                return null;
            }


            @Override
            public String getHelp() {
                return null;
            }
        });


        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "help";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                Output out = new Output(this);

                // no parameter is specified

                if (parameter == null || parameter.trim().equals("")) {

                    out.addLine("=============== Help Menu ===============");

                    out.addLine("Key:");
                    out.addLine("");

                    out.addLine("Specific parameters are denoted by the following: [start]");
                    out.addLine("These parameters must be typed exactly as they appear without the brackets.");
                    out.addLine("If a command accepts no parameters it is denoted by the following: [none]");
                    out.addLine("Different options are separated using a | character. Eg. [start | stop]");
                    out.addLine("Text that must be entered is denoted by the following: <recipientAddress>");

                    out.addLine("");
                    out.addLine("=============== Available Commands ===============");
                    out.addLine("");

                    for (CommandSet curr : register.getCommandSets()) {
                        for (Command currCommand : curr.getCommands()) {
                            appendCommandInfo(out, currCommand);
                        }
                    }

                    out.addLine("");
                    out.addLine("=============== Unavailable Commands ===============");
                    out.addLine("");


                    for (Command curr : register.getInvalidCommands()) {
                        appendCommandInfo(out, curr);
                    }

                } else {
                    // help requested for a specific command

                    out.addLine("Commands found for: " + parameter);

                    for (CommandSet curr : register.getCommandSets()) {
                        for (Command currCommand : curr.getCommands()) {

                            if (currCommand.getCommandName().equals(parameter)) {
                                appendCommandInfo(out, currCommand);
                                break;
                            } else {

                                for (String name : currCommand.getCommandAliases()) {
                                    if (name.equals(parameter)) {
                                        appendCommandInfo(out, currCommand);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
                queue.send(out);
            }

            @Override
            public String getUsage() {
                return "<command name>";
            }

            @Override
            public String[] getRequiredProperties() {
                return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getHelp() {
                return "Displays help for commands";
            }
        });

        return commands;
    }

    private void appendCommandInfo(Output out, Command command) {

        out.addLine("Command Name: " + command.getCommandName());

        for (String name : command.getCommandAliases()) {
            out.addLine("Command Aliases: " + name);
        }
        String usage = command.getCommandName();

        if (command.getUsage() != null) {
            usage += " ";
            usage += command.getUsage();
        }
        out.addLine("Usage: " + usage);
        StringBuilder required = new StringBuilder();

        required.append("Required Properties: ");

        if (command.getRequiredProperties() != null) {

            required.append(makeList(command.getRequiredProperties()));

        } else {
            required.append("None");
        }

        out.addLine(required.toString());

        StringBuilder optional = new StringBuilder();
        optional.append("Optional Properties: ");

        if (command.getOptionalProperties() != null) {

            optional.append(makeList(command.getOptionalProperties()));

        } else {
            optional.append("None");
        }

        out.addLine(optional.toString());

        out.addLine("Help: " + command.getHelp());

        out.addLine("------------------------------------");
    }


    private String makeList(String[] array) {
        StringBuilder list = new StringBuilder();

        for (String req : array) {
            list.append(req);
            list.append(", ");
        }
        return list.toString();
    }
}
