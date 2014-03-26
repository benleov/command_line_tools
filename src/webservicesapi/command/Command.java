package webservicesapi.command;

import webservicesapi.output.OutputQueue;

import java.util.Set;

/**
 * Represents a command, that performs an action, and returns
 * output.
 *
 * @author Ben Leov
 */
public interface Command {


    /**
     * Returns the primary name for this command.
     *
     * @return
     */
    String getCommandName();

    /**
     * Returns any short hand alias for this command.
     *
     * @return Returns any short hand alias for this command. Should return an empty set if there are none.
     */
    Set<String> getCommandAliases();

    /**
     * Executes this command.
     *
     * @param command   The command that was called.
     * @param parameter Anything that was typed by the user after the command.
     * @param queue     Queue to send output to.
     * @throws InvalidCommandException If the command was called in an incorrect manner.
     * @throws CommandErrorException   If there was an error trying to run this command caused by
     *                                 something external to the program.
     */
    void processCommand(String command, String parameter, OutputQueue queue) throws
            InvalidCommandException, CommandErrorException;


    /**
     * @return Returns a string describing how to use the command. It should not contain the name of the command
     *         itself.
     */
    String getUsage();

    /**
     * Returns help for this command, which should describe what it does.
     *
     * @return Returns help for this command, which should describe what it does.
     */
    String getHelp();


    /**
     * Stored properties that are required by this command in order to work correctly.
     * These are stored in the encrypted settings file.
     *
     * @return Returns the name of the properties required by this command.
     */
    String[] getRequiredProperties();


    /**
     * Returns optional properties. These are stored in the unencrypted settings file.
     */

    String[] getOptionalProperties();

}
