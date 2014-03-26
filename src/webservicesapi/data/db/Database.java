package webservicesapi.data.db;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Ben Leov
 */
public interface Database {


    boolean tableExists(String name) throws SQLException;

    void close();

    DataSource getDataSource();

    PreparedStatement prepare(String sql) throws SQLException;
}
