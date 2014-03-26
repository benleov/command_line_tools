package webservicesapi.command.impl.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.CommandSetRegister;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.command.impl.CommandBase;
import webservicesapi.command.impl.favorite.ChangeType;
import webservicesapi.command.impl.favorite.Favorite;
import webservicesapi.command.impl.favorite.FavoriteList;
import webservicesapi.command.impl.favorite.FavoriteListListener;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;
import webservicesapi.watch.CommandChangeListener;
import webservicesapi.watch.CommandChangeNotifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Periodically watches favorite commands, and notifies the user if the output has changed.
 */
public class NotificationCommandSet implements CommandSet {

    private static final Logger logger = LoggerFactory.getLogger(NotificationCommandSet.class);
    private FavoriteList favorites;
    private CommandSetRegister register;
    private final Map<String, CommandChangeNotifier> notifiers;

    public NotificationCommandSet(final FavoriteList favorites, CommandSetRegister register) {
        this.favorites = favorites;
        this.register = register;
        notifiers = new HashMap<String, CommandChangeNotifier>();

        favorites.addListener(new FavoriteListListener() {

            @Override
            public void onFavoriteChange(ChangeType type, String alias) {

                switch (type) {
                    case REMOVE: {
                        // stop notify and remove from HashMap if it exists

                        CommandChangeNotifier notifier = notifiers.get(alias);
                        if (notifier != null) {
                            notifier.stop();

                            // the notifier is removed from the hashmap by its listener onStop(), seen below.
                        }

                        break;
                    }
                    case ADD: {
                        // dont care if a new one is added
                        break;
                    }
                    case MODIFY: {
                        // pass modification onto notifier
                        CommandChangeNotifier notifier = notifiers.get(alias);

                        if (notifier != null) {
                            notifier.setParameter(favorites.getFavorite(alias).getParameters());
                        }

                        break;
                    }
                }
            }
        });

    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            final Command inner = this;

            @Override
            public String getCommandName() {
                return "watch";
            }

            @Override
            public void processCommand(String command, String parameter,
                                       final OutputQueue queue) throws InvalidCommandException {


                if (parameter.startsWith("add")) {

                    String[] split = parameter.split(" ");

                    if (split.length == 3) {

                        final String alias = split[1];
                        String time = split[2];
                        Favorite favorite = favorites.getFavorite(alias);

                        if (favorite != null) {

                            final CommandChangeNotifier watcher = new CommandChangeNotifier(Integer.parseInt(time));

                            Command favCommand = register.findByName(favorite.getCommandName());

                            if (favCommand != null) {
                                watcher.setCommand(favCommand);
                                watcher.setParameter(favorite.getParameters());

                                watcher.addListener(new CommandChangeListener() {

                                    @Override
                                    public void onCommandChanged(Output output) {
                                        queue.send(output);
                                    }

                                    @Override
                                    public void onStop() {
                                        // command notify has stopped. something must ahave gone with monitoring the
                                        // command. Remove it from the queue.

                                        notifiers.remove(alias);
                                        watcher.removeListener(this);
                                    }
                                });

                                notifiers.put(alias, watcher);

                                logger.info("Now watching command: " + favCommand.getCommandName());
                            } else {
                              throw new InvalidCommandException("Alias points to command that does not exist");  
                            }
                        } else {
                            throw new InvalidCommandException("No favorite with specified alias exists: " + alias);
                        }

                    } else {
                        throw new InvalidCommandException("Must provide timeout (milliseconds) and favorite alias");
                    }

                    Output out = new Output(this);
                    out.addLine("Favorite now watched");
                    queue.send(out);

                } else if (parameter.startsWith("remove")) {

                }
            }

            @Override
            public String getUsage() {
                return "[watch add <alias> <delay> | watch remove <alias>]";
            }

            @Override
            public String getHelp() {
                return "Watches favorite commands";
            }
        });
        return commands;
    }

}
