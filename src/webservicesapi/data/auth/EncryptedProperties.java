package webservicesapi.data.auth;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import webservicesapi.command.Command;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an encrypted properties file. The key is encrypted using SHA, which is then
 * decrypted using the users password. This key is then used to decrypt the properties
 * file using MD5 and DES.
 *
 * @author Ben Leov
 */
public class EncryptedProperties {

    private static Logger logger = LoggerFactory.getLogger(EncryptedProperties.class);

    private static final String KEY_ENCRYPT_METHOD = "SHA";
    private static final String CONTENT_ENCRYPT_METHOD = "PBEWithMD5AndDES";

    // one way hash of the password, to use for inital authentication 
    private static final String PROPERTY_KEY = "application.key";

    /**
     * Contains keys that should not be encrypted.
     */
    private static final Set<String> NON_ENCRYPTED = new HashSet<String>();

    /**
     * If set to true, the properties file will be set to clear text, assuming the properties
     * configuration file is authenticated correctly.
     */
    public static final String RESET_KEY = "application.reset";

    /**
     * The value that the reset key should be set to signify that the properties
     * contained within the file should be rest to clear text.
     */
    public static final String RESET_RESET_VALUE = "reset";

    /**
     * The value that the reset key should be set to to signify that the properties
     * contained within the file should be re-encrypted.
     */
    public static final String RESET_RESTORE_VALUE = "restore";

    static {
        NON_ENCRYPTED.add(PROPERTY_KEY);
        NON_ENCRYPTED.add(RESET_KEY);
    }

    private byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x34, (byte) 0xE3, (byte) 0x03
    };

    // Iteration count
    private int iterationCount = 19;


    private File file;
    private static Cipher cipher;
    private static Key key;
    private AlgorithmParameterSpec spec;

    private PropertiesConfiguration properties;

    /**
     * Creates a new Encrypted properties file if one doesnt exist; otherwise attempts to unlock
     * the existing properties file.
     *
     * @param file     The file that contains the encrypted properties.
     * @param password The password used to encrypt the file.
     * @throws AuthenticationException   Thrown if the specified password is invalid.
     * @throws IOException               If there is a problem reading/write from/to the file
     * @throws ConfigurationException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public EncryptedProperties(File file, byte[] password) throws AuthenticationException,
            IOException, ConfigurationException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException,
            BadPaddingException {

        this.file = file;

        cipher = Cipher.getInstance(CONTENT_ENCRYPT_METHOD);
        spec = new PBEParameterSpec(salt, iterationCount);

        MessageDigest digest = MessageDigest.getInstance(KEY_ENCRYPT_METHOD);

        String encrypted = new String(digest.digest(password)).replace(',', ' ');

        CharArrayWriter writer = new CharArrayWriter(password.length);

        for (byte curr : password) {
            writer.append((char) curr);
        }

        KeySpec keySpec = new PBEKeySpec(writer.toCharArray(), salt, iterationCount);
        key = SecretKeyFactory.getInstance(CONTENT_ENCRYPT_METHOD).generateSecret(keySpec);

        if (!file.exists()) {

            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // create new file

            if (!file.createNewFile()) {
                throw new IOException("File cannot be created");
            }

            // generate a new key

//            key = KeyGenerator.getInstance(CONTENT_ENCRYPT_METHOD).generateKey();


            // save key to file

            properties = new PropertiesConfiguration(file);

            // encrypt key and put in password file
//            properties.setProperty(CIPHER_KEY, encrypt(key.getEncoded()));
            // save one way hash of password
            properties.setProperty(PROPERTY_KEY, encrypted);

            // set restore property so the user can now enter in plain text properties
            // which will be encrypted on next login

            properties.setProperty(RESET_KEY, RESET_RESTORE_VALUE);

            properties.save();

        } else {

            properties = new PropertiesConfiguration(file);

            // load key
//            DESedeKeySpec spec = new DESedeKeySpec(decrypt(properties.getString(CIPHER_KEY)));
//
//            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(CONTENT_ENCRYPT_METHOD);
//            key = keyfactory.generateSecret(spec);

            // attempt to authenticate


            String existing = properties.getString(PROPERTY_KEY);

            if (!existing.equals(encrypted)) {
                throw new AuthenticationException("Invalid password");
            }

            String reset = properties.getString(RESET_KEY);

            if (reset != null && reset.equals(RESET_RESET_VALUE)) {
                decryptAll();

                // file cannot be used as its now decrypted. set the properties
                // decrypted to "encrypt", so that next time the file is loaded, it will be re-encrypted

                properties.setProperty(RESET_KEY, RESET_RESTORE_VALUE);
                properties.save();

                throw new AuthenticationException("Properties file has been decrypted. " +
                        "It now can be modified. Next time the file is loaded it will be re-encrypted");
            } else if (reset != null && reset.equals(RESET_RESTORE_VALUE)) {
                encryptAll();

                // removePosition property

                properties.clearProperty(RESET_KEY);
                properties.save();

            }


            // if set to unencrypted, when loaded, and authenticated the file should be reencrypted,
            // and this property removed.

        }
    }

    public boolean containsKey(Command cmd, String key) {
        return containsKey(cmd.getCommandName() + '#' + key);
    }

    public String containsKeys(Command cmd) {
        for (String key : cmd.getRequiredProperties()) {
            if (!containsKey(cmd, key)) {
                return key;
            }
        }
        return null;
    }


//****************************************************************************************************************

    public String getString(String key) {
        String encrypted = properties.getString(key);

        if (encrypted != null) {
            try {

                // as our authentication is validated when the properties file is created,
                // these exceptions should never happen unless theres a programing error.

                return new String(decrypt(encrypted));

            } catch (InvalidKeyException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            } catch (BadPaddingException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            } catch (IllegalBlockSizeException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            } catch (IOException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            } catch (InvalidAlgorithmParameterException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            }
        }
        return null;
    }

    public void setString(String key, String value) {
        try {
            properties.setProperty(key, encrypt(value.getBytes()));
        } catch (InvalidKeyException e) {
            logger.error("setString Encryption error! This is most likely a bug.", e);
        } catch (BadPaddingException e) {
            logger.error("setString Encryption error! This is most likely a bug.", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("setString Encryption error! This is most likely a bug.", e);
        } catch (InvalidAlgorithmParameterException e) {
            logger.error("getString Encryption error! This is most likely a bug.", e);
        }
    }

    public void save() {
        try {
            properties.save();
        } catch (ConfigurationException e) {
            logger.error("Error occurs while saving properties.", e);
        }
    }


    public void setGroup(String key, PropertyGroup value) {
        try {
            properties.setProperty(key, encrypt(value.getBytes()));
        } catch (InvalidKeyException e) {
            logger.error("setString Encryption error! This is most likely a bug.", e);
        } catch (BadPaddingException e) {
            logger.error("setString Encryption error! This is most likely a bug.", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("setString Encryption error! This is most likely a bug.", e);
        } catch (InvalidAlgorithmParameterException e) {
            logger.error("getString Encryption error! This is most likely a bug.", e);
        }
    }

        public PropertyGroup getGroup(String key) {
        String encrypted = properties.getString(key);

        if (encrypted != null) {
            try {

                // as our authentication is validated when the properties file is created,
                // these exceptions should never happen unless theres a programing error.

                PropertyGroup group = new PropertyGroup();
                group.fromBytes(decrypt(encrypted));
                return group;

            } catch (InvalidKeyException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            } catch (BadPaddingException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            } catch (IllegalBlockSizeException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            } catch (IOException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            } catch (InvalidAlgorithmParameterException e) {
                logger.error("getString Encryption error! This is most likely a bug.", e);
            }
        }
        return null;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    private void encryptAll() {
        // encrypt ALL except the key property
        Set<String> keys = properties.getLayout().getKeys();

        for (String curr : keys) {

            if (!NON_ENCRYPTED.contains(curr)) {
                String value = properties.getString(curr);
                setString(curr, value);
            }
        }
    }

    private void decryptAll() {
        // decrypt all properties EXCEPT the key property
        // TODO: this doesnt work!
        Set<String> keys = (Set<String>) properties.getLayout().getKeys();

        for (String curr : keys) {

            if (!NON_ENCRYPTED.contains(curr)) {
                String decrypted = getString(curr);
                properties.setProperty(curr, decrypted);
            }
        }
    }

    private String encrypt(byte[] input)
            throws InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        return new BASE64Encoder().encode(cipher.doFinal(input));
    }

    private byte[] decrypt(String encryptionBytes)
            throws InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException, IOException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(new BASE64Decoder().decodeBuffer(encryptionBytes));
    }
}
