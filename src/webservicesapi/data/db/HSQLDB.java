package webservicesapi.data.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.*;

/**
 * Represents an HSQL database, intended to implement a general db interface
 * in the future so this functionality can be generalised.
 *
 * @author Ben Leov
 */
public class HSQLDB implements Database {

    private static final Logger logger = LoggerFactory.getLogger(HSQLDB.class);
    private BasicDataSource source;
//    private Connection connection;

    /**
     * The hsql database file to use (or create if it doesnt exist).
     *
     * @param file
     * @throws SQLException
     */
    public HSQLDB(File file) throws SQLException {


        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        source = new BasicDataSource();
        source.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        source.setUsername("SA");
        source.setPassword("");
        source.setUrl("jdbc:hsqldb:file:" + file.getAbsolutePath());
        logger.trace("Accessing database at: " + file.getAbsolutePath());
    }


    /**
     * Closes the database.
     */
    @Override
    public void close() {

        try {
            source.close();
        } catch (SQLException e) {
            // ignore
        }
    }

    /**
     * Opens a connection from the data source, and returns a prepared statement.
     *
     * @param sql SQL to execute
     * @return The prepared statement.
     * @throws SQLException
     */
    public PreparedStatement prepare(String sql) throws SQLException {
        Connection conn = source.getConnection();
        return conn.prepareStatement(sql);
    }

    @Override
    public DataSource getDataSource() {
        return source;
    }

    public boolean tableExists(String name) throws SQLException {

        ResultSet tables = null;

        Connection connection = source.getConnection();
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            tables = dbm.getTables(null, null, name.toUpperCase(), null);
            return tables.next();
        } finally {
            if (tables != null) {
                tables.close();
            }

            if (connection != null) {
                connection.close();
            }
        }

    }

}
