package webservicesapi.command.impl;

import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandErrorException;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Checks an imap email account.
 *
 * @author Ben Leov
 */
public class IMAPCommandSet implements CommandSet {

    private EncryptedProperties configuration;

    public IMAPCommandSet(EncryptedProperties configuration) {
        this.configuration = configuration;
    }

    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();

        commands.add(new CommandBase() {

            /**
             * Pre-configured gmail checking command
             * @return
             */
            @Override
            public String getCommandName() {
                return "imap-gmail";
            }


            @Override
            public void processCommand(String command, String parameter, OutputQueue queue)
                    throws InvalidCommandException, CommandErrorException {

                Properties props = System.getProperties();

                try {
                    Session session = Session.getDefaultInstance(props, null);
                    Store store = session.getStore("imaps");

                    props.setProperty("mail.store.protocol", "imaps");


                    store.connect("imap.gmail.com",
                            configuration.getString("gmail.username"),
                            configuration.getString("gmail.password"));

                    Folder inbox = store.getFolder("inbox");

                    if (inbox == null) {
                        inbox = store.getDefaultFolder();
                    }

                    Output output = new Output(this);

                    appendInboxDetails(output, inbox);

                    queue.send(output);

                    store.close();
                } catch (NoSuchProviderException e) {
                    throw new InvalidCommandException(e);
                } catch (MessagingException e) {
                    throw new InvalidCommandException(e);
                } catch (IOException e) {
                    throw new InvalidCommandException(e);
                }
            }

            @Override
            public String getUsage() {
                return null;
            }

            @Override
            public String getHelp() {
                return "Checks the gmail account specified in the settings";
            }

            @Override
            public String[] getRequiredProperties() {
                return new String[]{"gmail.username", "gmail.password"};
            }
        });

        commands.add(new CommandBase() {

            /**
             * Generic imap checking command.
             * 
             * @return
             */
            @Override
            public String getCommandName() {
                return "imap-work";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {


                try {
                    Properties props = System.getProperties();
                    Session session = Session.getDefaultInstance(props, null);
                    props.setProperty("mail.store.protocol", "imap");
                    Store store = session.getStore("imap");
                    store.connect(
                            configuration.getString("imap.work.server"),
                            configuration.getString("imap.work.username"),
                            configuration.getString("imap.work.password"));

                    Folder inbox = store.getFolder("inbox");

                    if (inbox == null) {
                        inbox = store.getDefaultFolder();
                    }

                    Output output = new Output(this);

                    appendInboxDetails(output, inbox);

                    queue.send(output);

                    store.close();
                } catch (NoSuchProviderException e) {
                    throw new InvalidCommandException(e);
                } catch (MessagingException e) {
                    throw new InvalidCommandException(e);
                } catch (IOException e) {
                    throw new InvalidCommandException(e);
                }
            }

            @Override
            public String getUsage() {
                return null;
            }

            @Override
            public String[] getRequiredProperties() {
                return new String[]{"imap.work.server", "imap.work.username", "imap.work.password"};
            }

            @Override
            public String getHelp() {
                return "Checks an imap account.";
            }
        });

        return commands;
    }

    private void appendInboxDetails(Output output, Folder inbox) throws MessagingException, IOException {
        int unreadMessages = inbox.getUnreadMessageCount();
        output.addLine("You have " + unreadMessages + " unread emails.");
        if (unreadMessages > 0) {

            inbox.open(Folder.READ_ONLY);
            FlagTerm onlyRecent = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] unread = inbox.search(onlyRecent);

            for (Message curr : unread) {

                for (Address addr : curr.getFrom()) {
                    output.addLine("   From: " + addr.toString());
                }

                output.addLine("   Sent: " + curr.getSentDate());
                output.addLine("   Subject: " + curr.getSubject());

                // can only really init plain text on the command line

                if (curr.getContentType().toLowerCase().startsWith("multipart")) {

                    Multipart parts = (Multipart) curr.getContent();
                    for (int x = 0; x < parts.getCount(); x++) {
                        BodyPart p = parts.getBodyPart(x);
                        if (p.getContentType().toLowerCase().startsWith("text/plain")) {
                            writeOut(output, p.getInputStream());
                        }
                    }
                }

                if (curr.getContentType().toLowerCase().startsWith("text/plain")) {

                    writeOut(output, curr.getInputStream());

                }

                output.addLine("***********************");
            }
            inbox.close(false);
        }
    }

    /**
     * Writes contents of input stream to the output object.
     *
     * @param out
     * @param contentStream
     * @throws IOException
     */
    private void writeOut(Output out, InputStream contentStream) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(contentStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }

            out.addLine("   Content: " + buf.toString());
        } finally {
            buf.close();
            contentStream.close();
        }

    }
}
