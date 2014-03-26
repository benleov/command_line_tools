package webservicesapi.command.impl;

import webservicesapi.command.*;
import webservicesapi.command.impl.favorite.Favorite;
import webservicesapi.command.impl.favorite.FavoriteList;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides a way of saving shortcuts or alias's to commands.
 *
 * @author Ben Leov
 */


public class FavoritesCommandSet implements CommandSet {

    private CommandSetRegister register;
    private FavoriteList favorites;

    public FavoritesCommandSet(FavoriteList favorites, CommandSetRegister register) {
        this.favorites = favorites;
        this.register = register;
    }

    @Override
    public Set<Command> getCommands() {

        HashSet<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "fav";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException, CommandErrorException {

                Favorite favorite = favorites.getFavorite(parameter);

                if (favorite != null) {
                    register.handleCommand(favorite.getCommandName() + " " + favorite.getParameters());
                } else {
                    throw new InvalidCommandException("No favorite found with the alias: " + parameter);
                }
            }

            @Override
            public String getUsage() {
                return "<alias>";
            }

            @Override
            public String getHelp() {
                return "Runs a favorite (see fav-save for saving favorites)";
            }
        });

        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "fav-list";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException, CommandErrorException {

                Map<String, Favorite> all = favorites.getAllFavorites();

                Output out = new Output(this);

                out.addLine("*** All Favorites ***");

                for (String key : all.keySet()) {

                    out.addLine("Alias: " + key);
                    out.addLine("Command: " + all.get(key).getCommandName());
                    out.addLine("Parameters: " + all.get(key).getParameters());
                    out.addLine();
                }

                queue.send(out);

            }

            @Override
            public String getUsage() {
                return "";
            }

            @Override
            public String getHelp() {
                return "Lists all favorites";
            }
        });

        commands.add(new CommandBase() {

            @Override                       
            public String getCommandName() {
                return "fav-add";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException, CommandErrorException {

                if (parameter != null) {
                    String[] split = parameter.split(" ", 2);

                    if (split.length > 1) {

                        String alias = split[0];

                        // the full command including any parameters

                        String full = parameter.substring(alias.length() + 1);

                        String[] fullSplit = full.split(" ", 2);

                        if (fullSplit.length > 1) {
                            favorites.addFavorite(alias, fullSplit[0], fullSplit[1]);
                        } else if (fullSplit.length > 0) {
                            favorites.addFavorite(alias, fullSplit[0], null);
                        } else {
                            throw new InvalidCommandException("A command and alias must be specified.");
                        }

                        Output out = new Output(this);
                        out.addLine(("Alias Saved."));

                        queue.send(out);

                    } else {
                        throw new InvalidCommandException("An alias must be specified");
                    }
                } else {
                    throw new InvalidCommandException("Favorite alias and command must be specified");
                }
            }

            @Override
            public String getUsage() {
                return "<alias> <full command>";
            }

            @Override
            public String getHelp() {
                return "Saves favorite command";
            }
        });
        return commands;
    }
}
