package at.tuwien.dao;

import at.tuwien.entity.WordType;
import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.sql.*;

public class H2Handler {

    private static Connection connection = null;

    /**
     * Tries to open a connection to the database if no connection exists, if a
     * connection already exists than this connection will be returned
     *
     * @throws DaoException if connection could not been opened
     */
    private static void openConnection() throws DaoException {

        try {
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("jdbc:h2:~/cnl2asp");
            ds.setUser("sa");
            ds.setPassword("sa");
            connection = ds.getConnection();
        } catch (SQLException e) {
            throw new DaoException("Couldn't establish connection to database.");
        }
    }

    /**
     * Opens new connection to the database if no connection exists,
     * if a connection already exists than this connection will be returned
     *
     * @return connection to the database
     * @throws DaoException if connection couldn't be opened
     */
    public static Connection getConnection() throws DaoException {

        if (connection == null)
            openConnection();

        return connection;
    }

    /**
     * Closes the connection to the database.
     *
     * @throws SQLException if connection couldn't be closed
     */
    public static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static void executeSqlFile(String path) throws DaoException, SQLException {
        PreparedStatement preparedStatement = getConnection().prepareStatement("RUNSCRIPT FROM ?");
        preparedStatement.setString(1,path);
        preparedStatement.execute();
    }

    public static void setupDatabase() throws DaoException, SQLException {

        Statement stmt = getConnection().createStatement();
        PreparedStatement pst;
        ResultSet rs;

        stmt.execute("DROP TABLE IF EXISTS Word;");
        stmt.execute("DROP TABLE IF EXISTS TranslationPattern;");
        stmt.execute("DROP TABLE IF EXISTS ManualTranslation;");

        stmt.execute("DROP SEQUENCE IF EXISTS seq_wordID");
        stmt.execute("DROP SEQUENCE IF EXISTS seq_translationPatternID");
        stmt.execute("DROP SEQUENCE IF EXISTS seq_manualTranslationID");

        stmt.execute("CREATE SEQUENCE IF NOT EXISTS seq_wordID START WITH 0;");
        stmt.execute("CREATE TABLE IF NOT EXISTS Word (wordId BIGINT DEFAULT NEXTVAL('seq_wordID') PRIMARY KEY, word VARCHAR(255), wordType VARCHAR(255));");

        stmt.execute("CREATE SEQUENCE IF NOT EXISTS seq_translationPatternID START WITH 0;");
        stmt.execute("CREATE TABLE IF NOT EXISTS TranslationPattern (translationPatternId BIGINT DEFAULT NEXTVAL('seq_translationPatternID') PRIMARY KEY, nlSentence VARCHAR(1024), regex VARCHAR(1024), translation VARCHAR(1024));");

        pst = getConnection().prepareStatement("SELECT seq_translationPatternID.NEXTVAL from dual");
        rs = pst.executeQuery();
        if(rs.next()) {
            long seq_translationPatternID = rs.getLong(1);
            if(seq_translationPatternID == 0){
                executeSqlFile("translation_pattern_insert.sql");
            }
        }

        stmt.execute("CREATE SEQUENCE IF NOT EXISTS seq_manualTranslationID START WITH 0;");
        stmt.execute("CREATE TABLE IF NOT EXISTS ManualTranslation (manualTranslationId BIGINT DEFAULT NEXTVAL('seq_manualTranslationID') PRIMARY KEY, cnlSentence VARCHAR(1024), aspRule VARCHAR(1024));");

        connection.commit();
    }
}
