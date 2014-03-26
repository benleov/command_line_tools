package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides file system browsing support.
 *
 * @author Ben Leov
 */
public class FileCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(FileCommandSet.class);

    private File current;

    public FileCommandSet() {
        current = new File(System.getProperty("user.dir"));
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "ls";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                Output out = new Output(this);

                out.addLine("Current Directory:");
                out.addLine(current.getAbsoluteFile().getAbsolutePath());
                out.addLine("Listing Files:");

                if (current.listFiles() != null) {

                    for (File curr : current.listFiles()) {
                        out.addLine(curr.getAbsolutePath());
                    }
                } else {
                    out.addLine("INFO: No files found in directory.");
                }

                queue.send(out);
            }

            @Override
            public String getUsage() {
                return null;
            }

            public String getHelp() {
                return "lists a directories contents";
            }

            @Override
            public String[] getRequiredProperties() {
                return null;
            }
        });

        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "cd";
            }

            public void processCommand(String command, String params, OutputQueue queue) throws InvalidCommandException {

                Output out = new Output(this);

                if (params.equals("..")) {

                    if (current.getParentFile() != null) {
                        current = current.getParentFile();
                    }

                    out.addLine("Current Directory is now:");
                    out.addLine(current.getAbsolutePath());

                } else {
                    // attempt to change to that directory

                    String newDirectory;

                    if (!params.equals("/")) {
                        newDirectory = current.getAbsolutePath() + "/" + params;
                    } else {
                        newDirectory = params;
                    }

                    File change = new File(newDirectory);

                    if (change.exists() && change.isDirectory()) {
                        out.addLine("Changing to directory: " + change.getAbsolutePath());
                        current = change;
                    } else {
                        out.addLine("Directory does not exist: " + change.getAbsolutePath());
                    }
                }

                queue.send(out);
            }

            @Override
            public String getUsage() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public String getHelp() {
                return "changes directory";
            }

            public String[] getRequiredProperties() {
                return null;
            }
        });

        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "mkdir";
            }

            @Override
            public void processCommand(String command, String params, OutputQueue queue) throws InvalidCommandException {

                Output out = new Output(this);

                File f = new File(current.getAbsolutePath() + "/" + params);

                if (!f.exists()) {
                    if (f.mkdir()) {
                        out.addLine("Directory created!");
                    } else {
                        out.addLine("Could not create directory.");
                    }
                } else {
                    out.addLine("Directory already exists: " + f.getAbsolutePath());
                }

                queue.send(out);
            }

            @Override
            public String getUsage() {
                return "<directory name>";
            }

            @Override
            public String getHelp() {
                return "makes a new directory";
            }

            @Override
            public String[] getRequiredProperties() {
                return null;
            }
        });

        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "rmdir";
            }

            public void processCommand(String command, String params, OutputQueue queue) throws InvalidCommandException {
                File f = new File(current.getAbsolutePath() + "/" + params);

                Output out = new Output(this);

                if (f.exists()) {
                    if (f.delete()) {
                        out.addLine("Directory deleted!.");
                    } else {
                        out.addLine("Could not delete directory.");
                    }
                } else {
                    out.addLine("Directory does not exist: " + f.getAbsolutePath());
                }

                queue.send(out);
            }

            @Override
            public String getUsage() {
                return "<directory name>";
            }

            @Override
            public String getHelp() {
                return "Removes an empty directory";
            }
        });

        return commands;
    }
}
