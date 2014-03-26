package webservicesapi.data.db;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Ben Leov
 */
public class DatabaseTest {

    private static final File TEST_DB = new File("./db/testing.db");

    @BeforeMethod(groups = "database")
    private void testJavaSetup() throws SQLException {

        // setup

        Database database = new HSQLDB(TEST_DB);
        PreparedStatement s = database.prepare("DROP SCHEMA PUBLIC CASCADE");
        s.execute();
        s.close();

    }

    /**
     * Tests the row and column checker
     */
    @Test(groups = "database")
    public void testDatabase() throws SQLException {

        Database database = new HSQLDB(TEST_DB);

        // create table

        // commented due to removal of manual DB interaction (now done thru ibatis)

        PreparedStatement s1 = database.prepare("CREATE TABLE users ( id INTEGER IDENTITY, name VARCHAR(256), password VARCHAR(256));");
        s1.execute();
        s1.close();

//        // add user

        PreparedStatement s2 = database.prepare("INSERT INTO users (id, name, password) VALUES (1,'name','password')");
        s2.execute();
        s2.close();

//        // select user

        PreparedStatement s3 = database.prepare("SELECT * FROM users");
        s3.execute();
        ResultSet rs = s3.getResultSet();
        Assert.assertTrue(rs.next());
        s3.close();

        // check table exists method

        Assert.assertTrue(database.tableExists("users"));
        Assert.assertFalse(database.tableExists("table_should_not_exist"));
        database.close();
    }
}
