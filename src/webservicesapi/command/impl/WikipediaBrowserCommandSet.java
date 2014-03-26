package webservicesapi.command.impl;

import nu.xom.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.command.impl.browse.HTMLParser;
import webservicesapi.command.impl.browse.HTMLParserListener;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ben Leov
 */
public class WikipediaBrowserCommandSet implements CommandSet {

    private static final Logger logger = LoggerFactory.getLogger(WikipediaBrowserCommandSet.class);

    private static final int MAX_ARTICLE_LENGTH = 200;

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {


            @Override
            public String getCommandName() {
                return "wikipedia";
            }

            @Override
            public Set<String> getCommandAliases() {
                Set<String> name = new HashSet<String>();
                name.add("wiki");
                return name;
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException, CommandErrorException {
                try {
                    final StringBuffer buffer = new StringBuffer();

                    HTMLParser parser = new HTMLParser();
                    parser.addListener(new HTMLParserListener() {
                        public void onContentFound(String page, String content) {

                            if (buffer.length() < MAX_ARTICLE_LENGTH) {
                                buffer.append(content);
                            }
                        }

                        @Override
                        public void onFinish() {
                            buffer.append("\n Finished.");
                        }
                    });

                    parser.browse("http://en.wikipedia.org/wiki/" + parameter, "//html:div[@id='bodyContent']/html:p");

                    Output output = new Output(this);
                    output.addLine(buffer.toString());
                    queue.send(output);

                } catch (IOException e) {

                    throw new CommandErrorException(e);
                } catch (SAXException e) {
                    throw new CommandErrorException(e);
                } catch (ParsingException e) {
                    throw new CommandErrorException(e);
                }
            }

            @Override
            public String getUsage() {
                return "<topic>";
            }

            @Override
            public String getHelp() {
                return "Extracts content from wikipedia";
            }
        });


        return commands;
    }
}
