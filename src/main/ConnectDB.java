package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Creates connection to the database
 * @author Andre Simmons
 */
public class ConnectDB {
    private static PreparedStatement preparedStatement;

    /**
     * Database connection credentials
     */
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//localhost:3306/jdbc-c195";

    /**
     * Creates string for database connection url
     */
    private static final String URL = protocol + vendorName + ipAddress + "?serverTimeZone=SERVER";

    /**
     * JDBC Database Driver
     */
    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";

    public static Connection connection = null;

    /**
     * Database username and password
     */
    private static final String username = "root";
    private static final String password = "stop1362A*$$";

    /**
     * Function that starts the database connection
     * @return conn
     */
    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            connection = (Connection) DriverManager.getConnection(URL, username, password);
        } catch (ClassNotFoundException | SQLException e) {

        }
        return connection;

    }

    /**
     * Function that gets current database connection
     * @return current connection.
     */
    public static Connection getConnection() {

        return connection;
    }

    /**
     * Function to close the database connection
     */
    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Prepared Statement set function
     * @param c
     * @param sqlStatement
     * @throws SQLException
     */
    public static void setPreparedStatement(Connection c, String sqlStatement) throws SQLException {
        preparedStatement = c.prepareStatement(sqlStatement);
    }

    /**
     * Prepared Statement get function
     * @return
     */
    public static PreparedStatement getPreparedStatement() {return preparedStatement;}


}
