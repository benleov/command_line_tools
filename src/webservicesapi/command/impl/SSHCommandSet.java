package webservicesapi.command.impl;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.OutputQueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides SSH command support.
 *
 * @author Ben Leov
 */
public class SSHCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(SSHCommandSet.class);

    private EncryptedProperties properties;

    public SSHCommandSet(EncryptedProperties properties) {
        this.properties = properties;
    }

    @Override
    public Set<Command> getCommands() {

        HashSet<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return new String[]{"ssh.username", "ssh.host", "ssh.port", "ssh.password"};
            }

            @Override
            public String getCommandName() {
                return "ssh-put";
            }

            /**
             *
             * @param command   The command that was called.
             * @param parameter The name of the file to transmit to the remote server.
             * @param queue     Queue to send output to.
             * @throws InvalidCommandException
             * @throws CommandErrorException
             */
            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException, CommandErrorException {

                File toTransmit = new File(parameter);

                if (!toTransmit.exists()) {
                    throw new InvalidCommandException("Specified file does not exist.");
                }

                JSch jsch = new JSch();

                try {
                    Session session = jsch.getSession(
                            properties.getString("ssh.username"),
                            properties.getString("ssh.host"),
                            Integer.parseInt(properties.getString("ssh.port")));

                    session.setPassword(properties.getString("ssh.password"));

                    java.util.Properties config = new java.util.Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    session.connect();

                    logger.debug("Connected to remote server.");

                    Channel channel = session.openChannel("sftp");
                    channel.connect();
                    ChannelSftp shell = (ChannelSftp) channel;
                    shell.put(new FileInputStream(toTransmit), toTransmit.getName());

                    channel.disconnect();
                    session.disconnect();

                    logger.info("Upload complete.");

                } catch (JSchException e) {
                    throw new CommandErrorException(e);
                } catch (SftpException e) {
                    throw new CommandErrorException(e);
                } catch (FileNotFoundException e) {
                    throw new InvalidCommandException(e);
                }

            }

            @Override
            public String getUsage() {
                return "<file name>";
            }

            @Override
            public String getHelp() {
                return "Sends the specified file to to the ssh server. File is placed in the users " +
                        "home directory.";
            }
        });
        return commands;
    }
}