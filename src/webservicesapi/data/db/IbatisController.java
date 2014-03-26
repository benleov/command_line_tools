package webservicesapi.data.db;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;

/**
 * iBATIS is used for POJO to SQL translation.
 *
 * @author Ben Leov
 */
public class IbatisController {

    private static final Logger logger = LoggerFactory.getLogger(IbatisController.class);

    private SqlSessionFactory sqlSessionFactory;

    public IbatisController(DataSource source) {

        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        Environment environment =
                new Environment("development", transactionFactory, source);

        Configuration configuration = new Configuration(environment);

        try {
            parseXML(configuration, "webservicesapi.data.db.model.address.Contact");
            parseXML(configuration, "webservicesapi.data.db.model.note.Note");
        } catch (IOException e) {

            logger.error("Unable to locate xml model file", e);
            e.printStackTrace();
        }

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);


// TEST CODE --
//        SqlSession session = sqlSessionFactory.openSession();
//        session.insert("TestModel.insert", new TestModel());
//       session.select("webservicesapi.db.model.selectTest", 1,
//                new ResultHandler() {
//
//            @Override
//            public void handleResult(ResultContext resultContext) {
//
//                System.out.println("RESULT!: " + resultContext);
//
//            }
//        });

    }

    public SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }

    private void parseXML(Configuration configuration, String qualifiedClassName) throws IOException {

        Reader r = Resources.getResourceAsReader(qualifiedClassName.replace(".", "/") + ".xml");
        XMLMapperBuilder builder = new XMLMapperBuilder(r, configuration, qualifiedClassName, null);
        builder.parse();
    }
}
