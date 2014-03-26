package webservicesapi.command.impl;

import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.configuration.AbstractFileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An RSS Feed reader.
 *
 * @author Ben Leov
 */
public class RSSCommandSet implements CommandSet {

    private final Logger logger = LoggerFactory.getLogger(RSSCommandSet.class);
    private static final String SETTING_PREFIX = "rss.urls";

    private AbstractFileConfiguration store;

    public RSSCommandSet(AbstractFileConfiguration store) {
        this.store = store;
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getOptionalProperties() {
                return new String[]{SETTING_PREFIX};
            }

            @Override
            public String getCommandName() {
                return "rss";
            }
            // rss http://newsrss.bbc.co.uk/rss/newsonline_world_edition/front_page/rss.xml

            public void processCommand(String command, String parameter, OutputQueue queue) throws CommandErrorException,
                    InvalidCommandException {

                Output out = new Output(this);

                try {

                    if (parameter != null && !parameter.trim().equals("")) {

                        URL feedUrl = new URL(parameter);
                        SyndFeedInput input = new SyndFeedInput();
                        SyndFeed feed = input.build(new XmlReader(feedUrl));
                        out.addLine(feed.getTitle());
                        out.addLine(feed.getDescription());

                        List<SyndEntryImpl> entries = feed.getEntries();

                        for (SyndEntryImpl curr : entries) {
                            out.addLine("");
                            out.addLine("   " + curr.getTitle());
                            out.addLine("   " + curr.getLink());

                            List<Content> contents = curr.getContents();

                            for (Content content : contents) {
                                out.addLine("       " + content.toString());
                            }
                        }

                    } else {

                        List<String> urls = store.getList(SETTING_PREFIX);

                        if (urls.size() > 0) {
                            for (String url : urls) {

                                out.addLine("FEED: " + url);

                                URL feedUrl = new URL(url);
                                SyndFeedInput input = new SyndFeedInput();
                                SyndFeed feed = input.build(new XmlReader(feedUrl));
                                out.addLine(feed.getTitle());
                                out.addLine(feed.getDescription());

                                List<SyndEntryImpl> entries = feed.getEntries();

                                for (SyndEntryImpl curr : entries) {
                                    out.addLine("");
                                    out.addLine("   " + curr.getTitle());
                                    out.addLine("   " + curr.getLink());

                                    List<Content> contents = curr.getContents();

                                    for (Content content : contents) {
                                        out.addLine("       " + content.toString());
                                    }
                                }

                                out.addLine();

                            }
                        } else {
                            throw new InvalidCommandException("A url must be specified in the settings or on the " +
                                    "command line.");
                        }

                    }
                } catch (FeedException e) {
                    throw new CommandErrorException(e);
                } catch (MalformedURLException e) {
                    throw new CommandErrorException(e);
                } catch (IOException e) {
                    throw new CommandErrorException(e);
                }


                queue.send(out);
            }

            @Override
            public String getUsage() {
                return "<http://myrssfeed.com/rss>";
            }

            @Override
            public String getHelp() {
                return "Displays an RSS feed";
            }
        });
        return commands;
    }
}
