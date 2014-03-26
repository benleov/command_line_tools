package webservicesapi;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.data.auth.AuthenticationException;
import webservicesapi.data.auth.EncryptedProperties;
import webservicesapi.command.CommandSetRegister;
import webservicesapi.command.impl.favorite.FavoriteList;
import webservicesapi.data.db.Database;
import webservicesapi.data.db.HSQLDB;
import webservicesapi.data.db.IbatisController;
import webservicesapi.display.Screen;
import webservicesapi.display.ScreenException;
import webservicesapi.display.ScreenFactory;
import webservicesapi.input.CommandParser;
import webservicesapi.input.CommandParserListener;
import webservicesapi.input.CommandTracker;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;
import webservicesapi.output.OutputQueueListener;
import webservicesapi.output.Section;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;

/**
 * Starting point for the application.
 *
 * @author Ben Leov
 */
public class Main {

    /**
     * This file points to a manually created file that contains a list of supported
     * properties.
     */
    public static final String SUPPORTED_PROPERTIES_FILE = "supported_commands.txt";

    /**
     * Filename of the encrypted properties file, used to store
     * authentication details
     */
    public static final String PROPERTIES_FILENAME = "conf/settings.ini";

    /**
     * Unencrypted properties, for non sensitive details
     */
    public static final String CLEAR_PROPERTIES_FILENAME = "conf/unencryp_settings.ini";

    /**
     * Storage database file for use by commands.
     */
    public static final String DATABASE_NAME = "./db/storage.db";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Holds shared settings.
     */
    private static Database database;

    /**
     * Starts the application.
     * <p/>
     * If an empty encrypted file does not exist, it will be created.
     * The user can then enter their details in plain text.
     * <p/>
     * When the application is restarted, the file will be encrypted.
     * <p/>
     * If the user wishes to update the settings in the file, they must manually add
     * the restore flag. See the EncryptedProperties class for details. When the application
     * is restarted, the file will be decrypted, the user can then update the settings. When
     * the application is restarted again, the properties file will again be encrypted.
     *
     * @param arguments Main method arguments
     * @throws ConfigurationException
     * @throws IOException
     * @throws AuthenticationException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws ScreenException           If there is a problem displaying the screen.
     * @see EncryptedProperties#RESET_KEY
     * @see EncryptedProperties#RESET_RESET_VALUE
     */

    public static void main(String... arguments) throws ConfigurationException, IOException,
            AuthenticationException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException,
            BadPaddingException, ScreenException, SQLException {

        // load configuration file

        File properties = new File(PROPERTIES_FILENAME);

        logger.info("loading properties from: " + properties.getAbsolutePath());
        boolean propertiesExisted = properties.exists();

        if (propertiesExisted) {
            logger.info("Please enter your password.");
        } else {
            logger.info("Please enter a new password to encrypt your settings with.");
        }

        EncryptedProperties config = new EncryptedProperties(properties, readPassword().getBytes());

        if (propertiesExisted) {

            File clearSettings = new File(CLEAR_PROPERTIES_FILENAME);

            if (!clearSettings.exists()) {
                if (!clearSettings.createNewFile()) {
                    logger.warn("Unencrypted settings file could not be created: "
                            + clearSettings.getAbsolutePath());
                }
            }

            // clear settings file, for non sensitive data

            AbstractFileConfiguration clearProperties = new PropertiesConfiguration(clearSettings);
            clearProperties.setListDelimiter(',');

            // database for commands to use

            database = new HSQLDB(new File(DATABASE_NAME));
            IbatisController controller = new IbatisController(database.getDataSource());

            OutputQueue queue = new OutputQueue();

            // pass all output to the logger

            queue.addListener(new OutputQueueListener() {

                @Override
                public void onOutputReceived(Output received) {

                    // if the jcurses console is active its picked up the JCursesConsoleAppender
                    // if not, it goes directly to std out.

                    List<Section> sections = received.getSections();

                    for(Section curr : sections) { 
                        LoggerFactory.getLogger(received.getOwner()).info(curr.getOutput());
                        LoggerFactory.getLogger(received.getOwner()).info("Executed: " + curr.getTitle());
                    }

                }
            });

            // get the user interface

            Screen screen = new ScreenFactory(clearProperties).getScreen();

            // link with the command parser

            CommandParser parser = new CommandParser(screen);

            // tracks command history

            CommandTracker tracker = new CommandTracker();
            parser.addListener(tracker);

            // Create favorite list

            FavoriteList favorites = new FavoriteList();

            // bridge parser and command set register

            final CommandSetRegister register = new CommandSetRegister(config, favorites,
                    clearProperties, controller, tracker, parser.getScreen(), queue);

            parser.addListener(new CommandParserListener() {

                public void onLineRead(String read) {

                    boolean success = register.handleCommand(read);
                    if (!success) {
                        logger.info("No handler found for command: " + read);
                    }
                }
            });

            // start the command parser 

            logger.info("*** Console Ready ***");

            // app will wait here until quit is called

            parser.start();

           logger.info("*** Console Shutdown ***");

        } else {

            // init an error message pointing the user to the supported
            // commands file

            logger.error("Properties file did not exist. It has now been created. " +
                    "Please fill out the properties file and restart the application. It will" +
                    " be encrypted when you do so. See " +
                    SUPPORTED_PROPERTIES_FILE + " for a list of supported properties.");

        }
    }

    /**
     * The console is not always available. If it isnt we read directly from system.in.  Note: The reader
     * is not close as closing it will also close System.in.
     *
     * @return
     * @throws IOException
     */
    private static String readPassword() throws IOException {
        if (System.console() != null) {
            String password = new String(System.console().readPassword());
            return password;
        } else {
            logger.warn("*** Console not available. Your password may be printed on the screen as you type! ***");
            InputStreamReader reader = new InputStreamReader(System.in);
            BufferedReader buffer = new BufferedReader(reader);
            return buffer.readLine();
        }
    }
}
