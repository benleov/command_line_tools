package webservicesapi.command.impl.internal;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.*;
import webservicesapi.command.impl.CommandBase;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * A command that allows the user to modify the encrypted settings from the command line.
 *
 * @author Ben Leov
 */
public class ModifySettingsCommandSet implements CommandSet {

    private EncryptedProperties properties;
    private AbstractFileConfiguration clearProperties;
    private CommandSetRegister register;

    public ModifySettingsCommandSet(CommandSetRegister register,
                                    EncryptedProperties properties,
                                    AbstractFileConfiguration clearProperties) {
        this.properties = properties;
        this.clearProperties = clearProperties;
        this.register = register;
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return null;
            }

            @Override
            public String getCommandName() {
                return "set-encrypted";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                if (parameter == null) {
                    throw new InvalidCommandException("Requires 2 parameters");
                }
                String[] params = parameter.split(" ");

                if (params.length < 2) {
                    throw new InvalidCommandException("Requires 2 parameters");
                } else {
                    Output out = new Output(this);


                    // check if property is valid

                    if (register.requiredPropertyExists(params[0])) {
                        properties.setString(params[0], parameter.substring(params[0].length() + 1));
                        properties.save();
                        out.addLine("Property saved.");
                    } else {
                        out.addLine("Property " + params[0] + " is not a valid property");
                    }

                    queue.send(out);
                }
            }

            @Override
            public String getUsage() {
                return "<setting name> <value>";
            }

            @Override
            public String getHelp() {
                return "Allows encrypted properties to be set at runtime";
            }
        });
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return null;
            }

            @Override
            public String getCommandName() {
                return "set";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws
                    InvalidCommandException, CommandErrorException {

                if (parameter == null) {
                    throw new InvalidCommandException("Requires 2 parameters");
                }
                String[] params = parameter.split(" ");

                if (params.length < 2) {
                    throw new InvalidCommandException("Requires 2 parameters");
                } else {
                    Output out = new Output(this);


                    // check if property is valid

                    if (register.optionalPropertyExists(params[0])) {

                        clearProperties.setProperty(params[0], parameter.substring(params[0].length() + 1));
                        try {
                            clearProperties.save();
                        } catch (ConfigurationException e) {
                            throw new CommandErrorException(e);
                        }
                        out.addLine("Property saved.");
                    } else {
                        out.addLine("Property " + params[0] + " is not a valid property");
                    }

                    queue.send(out);
                }
            }

            @Override
            public String getUsage() {
                return "<setting name> <value>";
            }

            @Override
            public String getHelp() {
                return "Allows unencrypted properties to be set at runtime";
            }
        });
        return commands;
    }
}
