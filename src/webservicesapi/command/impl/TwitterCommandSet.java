package webservicesapi.command.impl;

import twitter4j.*;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ben Leov
 */
public class TwitterCommandSet implements CommandSet {

    private EncryptedProperties configuration;

    public TwitterCommandSet(EncryptedProperties configuration) {
        this.configuration = configuration;
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return new String[]{"twitter.username", "twitter.password"};
            }

            @Override
            public String getCommandName() {
                return "twitter";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                Twitter twitter = new Twitter(configuration.getString("twitter.username"),
                        configuration.getString("twitter.password"));

                Output out = new Output(this);

                try {

                    List<User> statuses = twitter.getFriendsStatuses();

                    if (parameter == null || parameter.trim().equals("") || parameter.equals("friends")) {

                        out.addLine("Friends");
                        out.addLine("name       status");
                        for (User curr : statuses) {
                            out.addLine(curr.getScreenName());
                            out.addLine(curr.getStatusText());

                        }
                    } else if (parameter.equals("messages")) {

                        out.addLine("Messages");

                        List<DirectMessage> messages = twitter.getDirectMessages();

                        if (messages.size() > 0) {

                            for (DirectMessage curr : messages) {
                                out.addLine("message from: " + curr.getSenderScreenName());
                                out.addLine(curr.getText());
                            }
                        } else {
                            out.addLine("No Messages");
                        }
                    } else if (parameter.equals("favorites")) {


                        List<Status> favorites = twitter.getFavorites();

                        out.addLine("Favorites");

                        for (Status curr : favorites) {
                            out.addLine("message from: " + curr.getUser().getScreenName());
                            out.addLine(curr.getText());
                        }
                    } else if (parameter.equals("trends")) {

                        out.addLine("Trends");

                        List<Trends> trends = twitter.getDailyTrends();

                        for (Trends trend : trends) {
                            for (Trend curr : trend.getTrends()) {
                                out.addLine("name: " + curr.getName());
                                out.addLine("query: " + curr.getQuery());
                            }
                        }
                    }

                    queue.send(out);

                } catch (TwitterException e) {
                    throw new InvalidCommandException(e);
                }

            }

            @Override
            public String getUsage() {
                return null;
            }

            @Override
            public String getHelp() {
                return "Displays twitter updates";
            }
        });
        return commands;

    }
}
