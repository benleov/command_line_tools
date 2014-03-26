package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.OutputQueue;

import javax.media.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Plays music via an external command.
 *
 * @author Ben Leov
 */
public class MusicCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(MusicCommandSet.class);

    private boolean internal = false;
    private int played = 0;
    private int current = 0;

    private EncryptedProperties configuration;

    public MusicCommandSet(EncryptedProperties configuration) {
        this.configuration = configuration;


        try {
            String MP3_CODEC = "com.sun.media.codec.audio.mp3.JavaDecoder";
            Codec music = (Codec) Class.forName(MP3_CODEC).newInstance();
//            Codec music = new JavaDecoder();
            PlugInManager.addPlugIn("mpegaudio",
                    music.getSupportedInputFormats(),
                    music.getSupportedOutputFormats(null),
                    PlugInManager.CODEC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return new String[]{"music.library", "music.player.command"};
            }

            @Override
            public String getCommandName()  {
                return "music";
            }

            @Override
            public void processCommand(String command, final String params, OutputQueue queue) throws InvalidCommandException {

                File music = new File(configuration.getString("music.library"));

                File[] dirs = music.listFiles(new FileFilter() {

                    public boolean accept(File file) {
                        return file.isDirectory() &&
                                file.getName().toLowerCase().startsWith(params.toLowerCase());
                    }
                });

                if (dirs == null || dirs.length == 0) {
                    logger.info("No directories found with given search parameter.");
                } else {
                    // attempt to find mp3 in this folder

                    File mp3 = null;

                    for (int x = 0; x < dirs.length && mp3 == null; x++) {
                        mp3 = findRandomMP3(dirs[x]);
                    }

                    if (mp3 == null) {
                        logger.info("No mp3s found with given search parameter.");
                    } else {
                        logger.info("Attemping to play: " + mp3.toURI());

                        // attempt to play the file internally; doesnt work with linux!

                        if (internal) {
                            try {
                                Player player = Manager.createPlayer(mp3.toURI().toURL());
                                player.start();
                            } catch (IOException e) {
                                throw new InvalidCommandException(e);
                            } catch (NoPlayerException e) {
                                throw new InvalidCommandException(e);
                            }
                        } else {

                            try {
                                Runtime.getRuntime().exec(
                                        configuration.getString("music.player.command") + " " + mp3.toURI());
                            } catch (IOException e) {
                                throw new InvalidCommandException(e);
                            }

                        }
                    }
                }
            }

            @Override
            public String getUsage() {
                return "<directory name>";
            }

            @Override
            public String getHelp() {
                return "Finds music files within the directory specified by the properties, and using" +
                        " the music player command, plays them";
            }
        });
        return commands;
    }

    private File findRandomMP3(File root) {

        current = 0; // the current iteration

        File mp3 = findMP3(root);

        if (mp3 == null) {
            System.out.println("Cannot find any more mp3s");
            // reset
            played = 0;
            current = 0;

            mp3 = findMP3(root);
        } else {
            played++;
        }

        return mp3;
    }

    private File findMP3(File root) {

        for (File curr : root.listFiles()) {
            if (curr.isFile() && curr.getName().endsWith("mp3")) {
                current++;

                if (current > played) {
                    return curr;
                }

            } else if (curr.isDirectory()) {
                return findMP3(curr);
            }
        }

        return null;
    }
}