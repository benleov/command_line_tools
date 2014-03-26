package webservicesapi.command.impl;

import com.slychief.javamusicbrainz.ServerUnavailableException;
import com.slychief.javamusicbrainz.entities.Artist;
import com.slychief.javamusicbrainz.entities.Release;
import com.slychief.javamusicbrainz.entities.ReleaseList;
import com.slychief.javamusicbrainz.entities.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides search operations for the music brains database. Allows the user to search for track or artist
 * information.
 * 
 * http://javamusicbrainz.sourceforge.net/
 */
public class MusicBrainzCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(SmsCommandSet.class);

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
                return "musicbrainz";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws
                    InvalidCommandException, CommandErrorException {

                String[] params = parameter.split(" ", 2);

                if (params.length > 1) {
                    Output out = new Output(this);

                    try {
                        if (params[0].equals("-a")) {
                            out.addLine("Searching for artist: " + params[1]);

                            List<Artist> artists = Artist.findByName(params[1]);
                            for (Artist curr : artists) {

                                out.addLine("Name: " + curr.getName());
                                out.addLine("Type: " + curr.getType());

                                if (curr.getDisambiguation() != null) {
                                    out.addLine("Disambiguation: " + curr.getDisambiguation());
                                }

                                ReleaseList releaseList = curr.getReleaseList();
                                if (releaseList != null) {
                                    List<Release> releases = releaseList.getReleases();

                                    for (Release release : releases) {
                                        out.addLine("   Release: " + release.getTitle());
                                    }
                                }

                                out.addLine("-------------------------");
                            }
                        } else if (params[0].equals("-t")) {
                            out.addLine("Searching for track: " + params[1]);

                            List<Track> tracks = Track.findByTitle(params[1]);

                            for (Track curr : tracks) {
                                Artist artist = curr.getArtist();
                                out.addLine("Artist: " + artist.getName());
                                out.addLine("Title: " + curr.getTitle());
                                out.addLine("Score: " + curr.getScore());
                                out.addLine("Duration: " + curr.getDuration());

                                out.addLine("-------------------------");
                            }

                        } else {
                            throw new InvalidCommandException("Must specify valid search type (-a for artist or -t for track)");
                        }

                        queue.send(out);

                    } catch (ServerUnavailableException e) {
                        throw new CommandErrorException(e);
                    }
                } else {
                    throw new InvalidCommandException("Must specify search type (-a for artist or -t for track)");
                }
            }

            @Override
            public String getUsage() {
                return "[-a|-t] <search>";
            }

            @Override
            public String getHelp() {
                return "Searches for artist(-a), or title(-t) information through the musicbrainz database";
            }
        });
        return commands;
    }
}