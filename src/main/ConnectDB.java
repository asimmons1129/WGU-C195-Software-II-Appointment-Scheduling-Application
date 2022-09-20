package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Creates the connection with the database
 * Given in the sample program from the lab
 * @author Andre Simmons
 */
public class ConnectDB {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static String password = "Passw0rd!";
    private static Connection connection = null;
    private static PreparedStatement preparedStatement;

    public static Connection startConnection(){
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");
        }
        catch(ClassNotFoundException e){
            System.out.println("Error: " + e.getMessage());
        }
        catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
        return connection;
    }

    public static Connection getConnection(){
        return connection;
    }

    public static void closeConnection(){
        try{
            connection.close();
            System.out.println("Connection closed!");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
        if(conn != null)
            preparedStatement = conn.prepareStatement(sqlStatement);
        else
            System.out.println("Prepared Statement Creation Failed");
    }

    public static PreparedStatement getPreparedStatement(){
        if(preparedStatement != null)
            return preparedStatement;
        else
            System.out.println("Null reference to Prepared Statement");
            return null;
    }
}
