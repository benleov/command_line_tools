package webservicesapi.data.db;

import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import webservicesapi.data.db.model.address.Contact;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Ben Leov
 */
public class IbatisControllerTest {

    private static final File TEST_DB = new File("./db/testing.db");


    @BeforeMethod(groups = "database")
    private void testJavaSetup() throws SQLException {

        // setup

        Database d = new HSQLDB(TEST_DB);

        PreparedStatement s = d.prepare("DROP SCHEMA PUBLIC CASCADE");
        s.execute();
        s.close();

        // create a new table for the contact

//        PreparedStatement s1 = d.prepare(new Contact().getTableCreateSQL());
//        s1.execute();
//        s1.close();
    }

    /**
     * Tests the row and column checker
     */
    @Test(groups = "ibatis")
    public void testDatabase() throws SQLException {

        Database d = new HSQLDB(TEST_DB);
        IbatisController controller = new IbatisController(d.getDataSource());

        SqlSession session = controller.openSession();
        session.insert("Contact.insert", new Contact());


    }
}
