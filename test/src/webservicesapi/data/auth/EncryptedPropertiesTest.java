package webservicesapi.data.auth;

import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Tests encrypted properties
 */
public class EncryptedPropertiesTest {

    private static final File TEST_FILE = new File("junit_encryptedfile.ini");

    @BeforeMethod(groups = "encryptedProperties")
    public void before() {
       TEST_FILE.delete();
    }

    @AfterMethod(groups = "encryptedProperties")
    public void after() {
      TEST_FILE.delete();
    }


    @Test(groups = "encryptedProperties")
    private void testPropertyGroup() throws IllegalBlockSizeException, IOException, InvalidKeyException,
            AuthenticationException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
            ConfigurationException, BadPaddingException {


        EncryptedProperties properties = new EncryptedProperties(TEST_FILE, "password".getBytes());


        PropertyGroup group = new PropertyGroup();

        group.addProperty("KEY1", "VALUE1");
        group.addProperty("KEY2", "VALUE2");
        group.addProperty("KEY3", "VALUE3");

        properties.setGroup("GROUP_KEY", group);

        PropertyGroup loaded = properties.getGroup("GROUP_KEY");

        Assert.assertEquals(group, loaded);
    }


}
