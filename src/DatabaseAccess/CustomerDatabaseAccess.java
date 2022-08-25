package DatabaseAccess;
import main.ConnectDB;
import model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accesses customers from database
 * @author Andre Simmons
 */
public class CustomerDatabaseAccess {
    /**
     * Creates observable list containing all customers from database
     * @throws SQLException
     * @return customersObservableList
     */
    public static ObservableList<Customer> loadAllCustomers(Connection connect) throws SQLException {

        String query = "SELECT Customers.Customer_ID, Customers.Customer_Name, Customers.Address, Customers.Postal_Code, Customers.Phone, Customers.Division_ID, FLDivisions.Division FROM Customers INNER JOIN  FLDivisions ON Customers.Division_ID = FLDivisions.Division_ID";
        PreparedStatement ps = ConnectDB.getConnection().prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        while (rs.next()) {
            int customerID = rs.getInt("Customer_ID");
            String name = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String postalCode = rs.getString("Postal_Code");
            String phoneNumber = rs.getString("Phone");
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            Customer c = new Customer(customerID, name, address, postalCode, phoneNumber, divisionID, divisionName);
            allCustomers.add(c);
        }
        return allCustomers;
    }

}
