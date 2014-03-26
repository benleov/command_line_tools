package webservicesapi.command;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.impl.*;
import webservicesapi.command.impl.favorite.FavoriteList;
import webservicesapi.command.impl.internal.ModifySettingsCommandSet;
import webservicesapi.command.impl.internal.NotificationCommandSet;
import webservicesapi.data.db.IbatisController;
import webservicesapi.display.Screen;
import webservicesapi.input.CommandTracker;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ben Leov
 */
public class CommandSetRegister {

    public enum COMMAND_LOCATION {
        VALID, INVALID, NON_EXISTANT;
    }

    private Logger logger = LoggerFactory.getLogger(CommandSetRegister.class);
    private Set<CommandSet> all;
    private Set<CommandSet> valid;
    private EncryptedProperties configuration;
    private AbstractFileConfiguration unencryptedConfiguration;
    private IbatisController database;
    private CommandTracker tracker;
    private FavoriteList favorites;
    private Screen screen;
    private OutputQueue outputQueue;

    /**
     * @param configuration            This is used for each commands required settings.
     * @param unencryptedConfiguration This is used for each commands optional settings.
     * @param database
     * @param tracker
     * @param screen
     * @param outputQueue
     */
    public CommandSetRegister(EncryptedProperties configuration,
                              FavoriteList favorites,
                              AbstractFileConfiguration unencryptedConfiguration,
                              IbatisController database,
                              CommandTracker tracker,
                              Screen screen,
                              OutputQueue outputQueue) {

        this.configuration = configuration;
        this.favorites = favorites;
        this.unencryptedConfiguration = unencryptedConfiguration;
        this.database = database;
        this.tracker = tracker;
        this.screen = screen;
        this.outputQueue = outputQueue;


        all = new HashSet<CommandSet>();

        all.add(new FileCommandSet());
        all.add(new ProgramCommandSet(configuration));
        all.add(new IMAPCommandSet(configuration));
        all.add(new EmailCommandSet(configuration));
        all.add(new FaceBookCommandSet(configuration));
        all.add(new SmsCommandSet());
        all.add(new TranslateCommandSet());
        all.add(new NoteCommandSet(database));
        all.add(new HelpCommandSet(this));
        all.add(new SearchCommandSet(configuration));
        all.add(new TwitterCommandSet(configuration));
        all.add(new TimeCommandSet(unencryptedConfiguration));
        all.add(new MusicCommandSet(configuration));
        all.add(new WikipediaBrowserCommandSet());
        all.add(new RSSCommandSet(unencryptedConfiguration));
        all.add(new AddressBookCommandSet(configuration, database));
        all.add(new CalendarCommandSet(configuration));
        all.add(new CalcCommandSet());
        all.add(new SystemInfoCommandSet());
        all.add(new NetworkCommandSet());
        all.add(new WeatherCommandSet(unencryptedConfiguration));
        all.add(new SkypeCommandSet());
        all.add(new SSHCommandSet(configuration));
        all.add(new DictionaryCommandSet());
        all.add(new AsenseCommandSet(configuration));
        all.add(new SudokuCommandSet());
        all.add(new JavaCommandSet(unencryptedConfiguration));
        all.add(new StopwatchCommandSet());
        all.add(new ModifySettingsCommandSet(this, configuration, unencryptedConfiguration));
        all.add(new FavoritesCommandSet(favorites, this));
        all.add(new CommandTrackerCommandSet(tracker, screen));
        all.add(new JavaAPIBrowserCommandSet());
        all.add(new MusicBrainzCommandSet());
        all.add(new NotificationCommandSet(favorites, this));

        valid = new HashSet<CommandSet>();
        revalidateCommands();
    }

    /**
     * Re populates the valid list of commands, making sure that each command has its required settings.
     */
    public void revalidateCommands() {
        valid.clear();
        Set<CommandSet> validated = findValidCommands(all);
        valid.addAll(validated);
    }

    public Set<Command> getInvalidCommands() {

        Set<CommandSet> invalidSets = new HashSet<CommandSet>();
        invalidSets.addAll(all);
        Set<CommandSet> validated = findValidCommands(all);
        invalidSets.removeAll(validated);

        Set<Command> invalid = new HashSet<Command>();

        for (CommandSet curr : invalidSets) {
            invalid.addAll(curr.getCommands());
        }
        return invalid;
    }

    private Set<CommandSet> findValidCommands(Set<CommandSet> sets) {

        Set<CommandSet> valid = new HashSet<CommandSet>();

        for (CommandSet curr : sets) {

            if (commandSetValid(curr)) {
                valid.add(curr);
            } else {
                logger.warn("Command set cannot function as required properties not found: " +
                        curr.getClass().getName());
            }
        }

        return valid;
    }

    public boolean commandSetValid(CommandSet set) {

        for (Command curr : set.getCommands()) {

            if (curr.getRequiredProperties() != null) {

                for (String required : curr.getRequiredProperties()) {
                    if (!configuration.containsKey(required)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    public Command findByName(String name) {
        for (CommandSet set : valid) {
            for (Command curr : set.getCommands()) {

                if (curr.getCommandName().equals(name)) {
                    return curr;
                }
            }
        }

        return null;

    }


    /**
     * Returns the Command l if the specified property is used by any command.
     *
     * @param property
     * @return
     */
    public boolean requiredPropertyExists(String property) {
        for (CommandSet set : all) {
            for (Command curr : set.getCommands()) {
                if (curr.getRequiredProperties() != null) {
                    for (String required : curr.getRequiredProperties()) {
                        if (required.equals(property)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean optionalPropertyExists(String property) {
        for (CommandSet set : all) {
            for (Command curr : set.getCommands()) {
                if (curr.getOptionalProperties() != null) {
                    for (String required : curr.getOptionalProperties()) {
                        if (required.equals(property)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Calls the appropriate command, with the given line read from the users input. If a command
     * is found, it is started in a new thread.
     *
     * @param fullCommand The command that the user typed
     * @return <code>true</code> if a command was found, false if not.
     */
    public boolean handleCommand(String fullCommand) {

        String[] commands = fullCommand.split(" ", 2);
        final String command = commands[0];
        final String params;

        if (commands.length > 1) {
            params = commands[1];
        } else {
            params = null;
        }

        for (CommandSet curr : all) {

            for (final Command com : curr.getCommands()) {

                if (com.getCommandName().equals(command) ||
                        com.getCommandAliases().contains(command)) {

                    new Thread(new Runnable() {

                        public void run() {
                            try {
                                com.processCommand(command, params, outputQueue);
                            } catch (InvalidCommandException e) {
                                logger.error("Invalid Command", e);
                            } catch (CommandErrorException e) {
                                logger.error("Command Error", e);
                            }
                        }
                    }).start();

                    return true;
                }
            }
        }

        return false;
    }

    public Set<CommandSet> getCommandSets() {
        return all;
    }
}
