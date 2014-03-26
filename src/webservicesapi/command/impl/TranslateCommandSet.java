package webservicesapi.command.impl;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ben Leov
 */
public class TranslateCommandSet implements CommandSet {

    // TODO: http://www.translation-guide.com/free_online_translators.php?from=English&to=Swedish
    // the above site supports better translation

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
                return "translate";
            }

            @Override
            public void processCommand(String command, String params, OutputQueue queue) throws InvalidCommandException {
                Translate.setHttpReferrer("www.myurl.com");

                Language language = null;
                String text;

                if (params != null && params.split(" ", 2).length > 1) {

                    language = Language.fromString(params.split(" ")[0]);

                    // from String didnt work; try our own look up tables
                    if (language == null) {
                        language = fromString(params.split(" ")[0]);
                    }
                }

                if (language != null) {
                    text = params.substring(params.split(" ")[0].length());
                } else {
                    language = Language.SWEDISH;
                    text = params;
                }

                try {
                    Output out = new Output(this);

                    String translated = Translate.execute(text, Language.ENGLISH, language);

                    if (translated != null && !translated.trim().equals("")) {
                        out.addLine(language.name() + ": " + translated);
                    } else {
                      out.addLine("Cannot translate that word.");  
                    }

                    queue.send(out);
                } catch (Exception e) {   // throws a java.lang.Exception
                    throw new InvalidCommandException(e);
                }
            }

            @Override
            public String getUsage() {
                return "<word> | <language> <word>";
            }

            @Override
            public String getHelp() {
                return "Translates from english to a variety of languages";
            }
        });


        return commands;
    }

    private Language fromString(String language) {
        if (language != null) {
            for (Language curr : Language.values()) {
                if (curr.name().equalsIgnoreCase(language)) {
                    return curr;
                }
            }
        }

        return null;
    }

}
