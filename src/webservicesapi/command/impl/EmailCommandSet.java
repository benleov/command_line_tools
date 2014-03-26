package webservicesapi.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.output.OutputQueue;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


/**
 * Simple email sender.
 *
 * @author Ben Leov
 */
public class EmailCommandSet implements CommandSet {

    private Logger logger = LoggerFactory.getLogger(EmailCommandSet.class);

    private EncryptedProperties configuration;

    public EmailCommandSet(EncryptedProperties configuration) {
        this.configuration = configuration;
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "email";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                if (parameter != null) {
                    String[] split = parameter.split("\\|");

                    logger.info("Sending email to: " + split[0]);
                    logger.info("Sending subject: " + split[1]);
                    logger.info("Sending body: " + split[2]);

                    SendMail mail = new SendMail(configuration.getString("email.from"), split[0], split[1], split[2]);
                    mail.send();

                    logger.info("email sent.");
                } else {
                    throw new InvalidCommandException("Command requires a recipient, subject and message body.");
                }
            }

            @Override
            public String getUsage() {
                return "<recipientAddress> | <subject> | <message body>";
            }

            @Override
            public String getHelp() {
                return "Sends an email via an SMTP server";
            }

            @Override
            public String[] getRequiredProperties() {
                return new String[]{"email.smtp", "email.port", "email.from"};
            }
        });
        return commands;
    }

    public class SendMail {

        private String from;
        private String to;
        private String subject;
        private String text;

        public SendMail(String from, String to, String subject, String text) {
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.text = text;
        }

        public void send() throws InvalidCommandException {

            Properties props = new Properties();
            props.put("mail.smtp.host", configuration.getString("email.smtp"));
            props.put("mail.smtp.port", configuration.getString("email.port"));

            Session mailSession = Session.getInstance(props);

            Message simpleMessage = new MimeMessage(mailSession);

            InternetAddress fromAddress;
            InternetAddress toAddress;
            try {
                fromAddress = new InternetAddress(from);
                toAddress = new InternetAddress(to);
            } catch (AddressException e) {
                throw new InvalidCommandException(e);
            }

            try {
                simpleMessage.setFrom(fromAddress);
                simpleMessage.setRecipient(RecipientType.TO, toAddress);
                simpleMessage.setSubject(subject);
                simpleMessage.setText(text);

                Transport.send(simpleMessage);
            } catch (MessagingException e) {
                throw new InvalidCommandException(e);
            }
        }
    }
}
