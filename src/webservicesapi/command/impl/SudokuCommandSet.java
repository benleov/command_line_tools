package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.command.impl.sudoku.SudokuBuilder;
import webservicesapi.command.impl.sudoku.SudokuGame;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * Note: Unimplemented.
 * A simple Sudoku game.
 *
 * @author Ben Leov
 */
public class SudokuCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(SudokuCommandSet.class);

    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            public String[] getRequiredProperties() {
                return null;
            }

            @Override
            public String getCommandName()  {
                return "sudoku";
            }

            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                if (parameter.equals("start")) {
                    SudokuGame game = new SudokuBuilder().buildGame();
                    logger.info(game.toString());
                }
            }

            @Override
            public String getUsage() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public String getHelp() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        return commands;
    }
}
