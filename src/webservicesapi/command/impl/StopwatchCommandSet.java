package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple stopwatch command set.
 *
 * @author Ben Leov
 */
public class StopwatchCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(StopwatchCommandSet.class);

    private Date start;
    private Date stop;

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
                return "stopwatch";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                Output out = new Output(this);

                if (parameter.equals("start")) {
                    if (start == null || stop != null) {
                        start = new Date();
                        stop = null;
                        out.addLine("StopWatch started.");
                    } else {
                        out.addLine("StopWatch has already started.");
                    }
                } else if (parameter.equals("stop")) {

                    if (start != null) {
                        out.addLine("StopWatch stopped.");
                        stop = new Date();

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(stop.getTime() - start.getTime());
                        out.addLine("time: " + cal.get(Calendar.SECOND) + " seconds");

                    } else {
                        out.addLine("StopWatch has not been started.");
                    }
                } else if (parameter.equals("time")) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(new Date().getTime() - start.getTime());
                    out.addLine("time: " + cal.get(Calendar.SECOND) + " seconds");
                } else {
                    throw new InvalidCommandException("Invalid parameter specified");
                }

                queue.send(out);
            }

            @Override
            public String getUsage() {
                return "[start] | [stop] | [time]";
            }

            @Override
            public String getHelp() {
                return "Provides simple stopwatch functionality";
            }
        });
        return commands;
    }
}
