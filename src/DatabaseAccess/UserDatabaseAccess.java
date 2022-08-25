package DatabaseAccess;
import main.ConnectDB;
import model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accesses users from database
 */
public class UserDatabaseAccess extends User{
    public UserDatabaseAccess(int userID, String username, String password) {
        super(userID, username, password);
    }

    /**
     * Verifies validity of user based on info from database
     * @param un
     * @param p
     */
    public static int validateUser(String un, String p)
    {
        try
        {
            String sqlQuery = "SELECT * FROM Users WHERE User_Name = '" + un + "' AND Password = '" + p +"'";
            PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getString("User_Name").equals(un))
            {
                if (rs.getString("Password").equals(p))
                {
                    return rs.getInt("User_ID");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Creates observable list containing all user info from database
     * @throws SQLException
     * @return usersObservableList
     */
    public static ObservableList<UserDatabaseAccess> loadAllUsers() throws SQLException {
        ObservableList<UserDatabaseAccess> allUsers = FXCollections.observableArrayList();
        String sqlData = "SELECT * from Users";
        PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlData);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            String username = rs.getString("User_Name");
            String password = rs.getString("Password");
            UserDatabaseAccess u = new UserDatabaseAccess(userID, username, password);
            allUsers.add(u);
        }
        return allUsers;
    }
}
