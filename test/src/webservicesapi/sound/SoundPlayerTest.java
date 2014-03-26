package webservicesapi.sound;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import webservicesapi.command.impl.JavaCommandSet;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: developer
 * Date: May 25, 2010
 * Time: 3:10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SoundPlayerTest {

    @BeforeMethod(groups = "java_command")
    private void testJavaSetup() {
        // setup
    }

    /**
     * Tests the row and column checker
     */
    @Test(groups = "java_command")
    public void testGetProfileName() throws IOException {

        new SoundPlayer().play();
    }

}
