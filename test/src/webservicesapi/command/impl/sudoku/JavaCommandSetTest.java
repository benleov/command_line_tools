package webservicesapi.command.impl.sudoku;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import webservicesapi.command.impl.JavaCommandSet;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Ben Leov
 */
public class JavaCommandSetTest {

    @BeforeMethod(groups = "java_command")
    private void testJavaSetup() {
        // setup
    }

    /**
     * Tests the row and column checker
     */
    @Test(groups = "java_command")
    public void testGetProfileName() {
        JavaCommandSet command = new JavaCommandSet(null);
        assertEquals("myProfile", command.getProfileName("java.key.myProfile"));
        assertEquals("anotherProfile", command.getProfileName("java.key.anotherProfile"));
        assertEquals(null, command.getProfileName("java.key"));
    }


}
