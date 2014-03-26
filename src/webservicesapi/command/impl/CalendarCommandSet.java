package webservicesapi.command.impl;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.Content;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Google Calendar checker. By default displays events occuring today.
 *
 * @author Ben Leov
 */
public class CalendarCommandSet implements CommandSet {

    private EncryptedProperties configuration;

    public CalendarCommandSet(EncryptedProperties configuration) {
        this.configuration = configuration;
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return new String[]{"gmail.username", "gmail.password"};
            }

            public String getCommandName()  {
                return "calendar";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException, CommandErrorException {

                if (parameter == null || parameter.equals("today")) {

                    CalendarService myService = new CalendarService("exampleCo-exampleApp-1");
                    try {
                        myService.setUserCredentials(configuration.getString("gmail.username"),
                                configuration.getString("gmail.password"));

                        DateTime min = new DateTime(new Date());
                        min.setDateOnly(true);
                        DateTime max = new DateTime(new Date().getTime() + 86400000);
                        max.setDateOnly(true);

                        List<CalendarEntry> entries = getCalendarEntries(myService, min, max);

                        Output out = new Output(this);
                        out.addLine("Calendar Entries for today: " + entries.size());

                        for (int x = 0; x < entries.size(); x++) {
                            CalendarEntry curr = entries.get(x);

                            out.addLine(curr.getTitle().getPlainText());

                            if (curr.getSummary() != null) {
                                out.addLine(curr.getSummary().getPlainText());
                            }

                            if (curr.getContent() != null) {
                                if (curr.getContent().getType() == Content.Type.TEXT) {
                                    TextContent content = (TextContent) curr.getContent();
                                    out.addLine(content.getContent().getPlainText());
                                }
                            }
                        }

                        queue.send(out);

                    } catch (AuthenticationException e) {
                        throw new InvalidCommandException(e);
                    } catch (MalformedURLException e) {
                        throw new CommandErrorException(e);
                    } catch (IOException e) {
                        throw new CommandErrorException(e);
                    } catch (ServiceException e) {
                        throw new CommandErrorException(e);
                    }
                }
            }

            @Override
            public String getUsage() {
                return "[date | today]";
            }

            public String getHelp() {
                return "Provides Google Calendar functions";
            }
        });
        return commands;
    }

    public List<CalendarEntry> getCalendarEntries(CalendarService myService, DateTime min, DateTime max) throws
            IOException, ServiceException {

        URL feedUrl = new URL("http://www.google.com/calendar/feeds/" +
                configuration.getString("gmail.username") + "/private/full");

        CalendarQuery myQuery = new CalendarQuery(feedUrl);

        myQuery.setMinimumStartTime(min);
        myQuery.setMaximumStartTime(max);

        CalendarFeed resultFeed = myService.getFeed(myQuery, CalendarFeed.class);
        return resultFeed.getEntries();
    }
}

