package webservicesapi.command.impl;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.Im;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.data.db.IbatisController;
import webservicesapi.output.OutputQueue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * For notes on how to use the google address book api.
 * <p/>
 * http://code.google.com/apis/contacts/docs/3.0/developers_guide_java.html#GettingStarted
 *
 * @author Ben Leov
 */
public class AddressBookCommandSet implements CommandSet {

    private final Logger logger = LoggerFactory.getLogger(AddressBookCommandSet.class);

    private final EncryptedProperties configuration;
    private final IbatisController database;

    public AddressBookCommandSet(EncryptedProperties configuration,
                                 IbatisController database) {
        this.configuration = configuration;
        this.database = database;

        prepare(database);
    }

    private void prepare(IbatisController database) {

//        database.tableExists("")


    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            public String[] getRequiredProperties() {
                return new String[]{"gmail.username", "gmail.password"};
            }

            @Override
            public String getCommandName() {
                return "address";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue)
                    throws InvalidCommandException {

                ContactsService myService = new ContactsService("exampleCo-exampleApp-1");

                try {
                    if (parameter.equals("list")) {

                        myService.setUserCredentials(configuration.getString("gmail.username"),
                                configuration.getString("gmail.password"));

                        printAllContacts(myService);

                    } else if (parameter.equals("sync")) {
                        myService.setUserCredentials(configuration.getString("gmail.username"),
                                configuration.getString("gmail.password"));

                        List<ContactEntry> list = getAllContacts(myService);

                        int x = 0;

                        for (ContactEntry curr : list) {

                            List<String> contactLine = new ArrayList<String>();

                            // extract a name out for this contact

                            String name = null;

                            if (curr.getName() != null) {

                                if (curr.getName().hasGivenName()) {
                                    name = curr.getName().getGivenName().getValue();

                                    if (curr.getName().hasFamilyName()) {
                                        name += " ";
                                        name += curr.getName().getFamilyName().getValue();
                                    }
                                } else if (curr.getName().getFullName() != null) {
                                    name = curr.getName().getFullName().getValue();
                                }


                            }

                            // still haven't got a name

                            if (name == null) {
                                if (curr.getShortName() != null) {
                                    name = curr.getShortName().getValue();
                                } else if (curr.getNickname() != null) {
                                    name = curr.getNickname().getValue();
                                } else {
                                    logger.info("Cannot find name for contact. Ignoring.");
                                    continue;
                                }
                            }

                            contactLine.add(name);

                            if (curr.getEmailAddresses().size() > 0) {
                                contactLine.add(curr.getEmailAddresses().get(0).getAddress());
                            } else {
                                contactLine.add("none");
                            }

                            if (curr.getPhoneNumbers().size() > 0) {
                                contactLine.add(curr.getPhoneNumbers().get(0).getPhoneNumber());
                            } else {
                                contactLine.add("none");
                            }


                            // TODO: Update to use database 
//                            database.addProperty(String.valueOf(x++), contactLine);
                        }

//                        database.save();

                        logger.info("Contacts sync complete");
                    }
                } catch (AuthenticationException e) {
                    throw new InvalidCommandException(e);
                } catch (IOException e) {
                    throw new InvalidCommandException(e);
                } catch (ServiceException e) {
                    throw new InvalidCommandException(e);
                }
            }

            @Override
            public String getUsage() {
                return "list";
            }

            public String getHelp() {
                return "Imports contacts from Google";
            }
        });
        return commands;
    }

    public List<ContactEntry> getAllContacts(ContactsService myService)
            throws IOException, ServiceException {
        URL feedUrl = new URL("http://www.google.com/m8/feeds/contacts/" +
                configuration.getString("gmail.username") + "/full");
        ContactFeed resultFeed = myService.getFeed(feedUrl, ContactFeed.class);
        return resultFeed.getEntries();
    }

    public void printAllContacts(ContactsService myService)
            throws ServiceException, IOException {
        // Request the feed
        List<ContactEntry> list = getAllContacts(myService);

        // Print the results
        for (ContactEntry entry : list) {
            if (entry.hasName()) {
                Name name = entry.getName();
                if (name.hasFullName()) {
                    String fullNameToDisplay = name.getFullName().getValue();
                    if (name.getFullName().hasYomi()) {
                        fullNameToDisplay += " (" + name.getFullName().getYomi() + ")";
                    }
                    System.out.println("\t\t" + fullNameToDisplay);
                } else {
                    System.out.println("\t\t (no full name found)");
                }
                if (name.hasNamePrefix()) {
                    System.out.println("\t\t" + name.getNamePrefix().getValue());
                } else {
                    System.out.println("\t\t (no name prefix found)");
                }
                if (name.hasGivenName()) {
                    String givenNameToDisplay = name.getGivenName().getValue();
                    if (name.getGivenName().hasYomi()) {
                        givenNameToDisplay += " (" + name.getGivenName().getYomi() + ")";
                    }
                    System.out.println("\t\t" + givenNameToDisplay);
                } else {
                    System.out.println("\t\t (no given name found)");
                }
                if (name.hasAdditionalName()) {
                    String additionalNameToDisplay = name.getAdditionalName().getValue();
                    if (name.getAdditionalName().hasYomi()) {
                        additionalNameToDisplay += " (" + name.getAdditionalName().getYomi() + ")";
                    }
                    System.out.println("\t\t" + additionalNameToDisplay);
                } else {
                    System.out.println("\t\t (no additional name found)");
                }
                if (name.hasFamilyName()) {
                    String familyNameToDisplay = name.getFamilyName().getValue();
                    if (name.getFamilyName().hasYomi()) {
                        familyNameToDisplay += " (" + name.getFamilyName().getYomi() + ")";
                    }
                    System.out.println("\t\t" + familyNameToDisplay);
                } else {
                    System.out.println("\t\t (no family name found)");
                }
                if (name.hasNameSuffix()) {
                    System.out.println("\t\t" + name.getNameSuffix().getValue());
                } else {
                    System.out.println("\t\t (no name suffix found)");
                }
            } else {
                System.out.println("\t (no name found)");
            }

            System.out.println("Email addresses:");
            for (Email email : entry.getEmailAddresses()) {
                System.out.print(" " + email.getAddress());
                if (email.getRel() != null) {
                    System.out.print(" rel:" + email.getRel());
                }
                if (email.getLabel() != null) {
                    System.out.print(" label:" + email.getLabel());
                }
                if (email.getPrimary()) {
                    System.out.print(" (primary) ");
                }
                System.out.print("\n");
            }

            System.out.println("IM addresses:");
            for (Im im : entry.getImAddresses()) {
                System.out.print(" " + im.getAddress());
                if (im.getLabel() != null) {
                    System.out.print(" label:" + im.getLabel());
                }
                if (im.getRel() != null) {
                    System.out.print(" rel:" + im.getRel());
                }
                if (im.getProtocol() != null) {
                    System.out.print(" protocol:" + im.getProtocol());
                }
                if (im.getPrimary()) {
                    System.out.print(" (primary) ");
                }
                System.out.print("\n");
            }

            System.out.println("Groups:");
            for (GroupMembershipInfo group : entry.getGroupMembershipInfos()) {
                String groupHref = group.getHref();
                System.out.println("  Id: " + groupHref);
            }

            System.out.println("Extended Properties:");
            for (ExtendedProperty property : entry.getExtendedProperties()) {
                if (property.getValue() != null) {
                    System.out.println("  " + property.getName() + "(value) = " +
                            property.getValue());
                } else if (property.getXmlBlob() != null) {
                    System.out.println("  " + property.getName() + "(xmlBlob)= " +
                            property.getXmlBlob().getBlob());
                }
            }

            String photoLink = entry.getContactPhotoLink().getHref();
            System.out.println("Photo Link: " + photoLink);

            if (photoLink != null) {
                System.out.println("Contact Photo's ETag: " + entry.getContactPhotoLink().getEtag());
            }

            System.out.println("Contact's ETag: " + entry.getEtag());
        }
    }
}