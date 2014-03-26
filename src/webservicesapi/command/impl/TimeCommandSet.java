package webservicesapi.command.impl;

import net.sf.atomicdate.Date;
import org.apache.commons.configuration.AbstractFileConfiguration;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Displays the time, synced from a time server on the net. (see net.sf.atomicdate for details).
 * The user can specify which timezones are displayed by default in the unencrypted settings file.
 *
 * @author Ben Leov
 */
public class TimeCommandSet implements CommandSet {

    private static final String SETTING_PREFIX = "time.timezones";

    private AbstractFileConfiguration store;

    public TimeCommandSet(AbstractFileConfiguration store) {
        this.store = store;
    }

    @Override
    public Set<Command> getCommands() {
        Set<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "time";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                Output out = new Output(this);

                // note the Date object is a net.sf.atomicdate.Date,
                // which retrieves the current time from the internet

                Date date = new Date();
                Calendar cal = Calendar.getInstance();

                cal.setTime(date);
                String strFormat = "dd.MM.yyyy 'at' h:mm:ss a zzzz";

                DateFormat format = new SimpleDateFormat(strFormat);

                if (parameter != null && !parameter.trim().equals("")) {
                    // user has specified timezone on the command line

                    TimeZone specified = TimeZone.getTimeZone(parameter);
                    format.setTimeZone(specified);
                    out.addLine(specified.getDisplayName() + " : " + format.format(cal.getTime()));
                    
                } else {

                    // settings exist

                    List<String> zones = store.getList(SETTING_PREFIX);

                    if (zones == null || zones.size() == 0) {

                        // print the default

                        out.addLine("Local: " + format.format(cal.getTime()));

                        format.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
                        out.addLine("Sweden: " + format.format(cal.getTime()));

                        format.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
                        out.addLine("Australia: " + format.format(cal.getTime()));

                        format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                        out.addLine("America/Los Angeles: " + format.format(cal.getTime()));

                        format.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
                        out.addLine("New Zealand: " + format.format(cal.getTime()));
                    } else {

                        for (String zone : zones) {
                            format.setTimeZone(TimeZone.getTimeZone(zone));
                            out.addLine(zone + ": " + format.format(cal.getTime()));
                        }
                    }

                }
                queue.send(out);
            }

            @Override
            public String getUsage() {
                return null;
            }

            @Override
            public String getHelp() {
                return "Prints the current time";
            }

            @Override
            public String[] getOptionalProperties() {
                return new String[]{SETTING_PREFIX};
            }
        });

        return commands;
    }
}
