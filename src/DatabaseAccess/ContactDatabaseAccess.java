package DatabaseAccess;
import main.ConnectDB;
import model.Contact;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accesses contacts in database
 * @author Andre Simmons
 */
public class ContactDatabaseAccess {
    /**
     * Create an observable list containing all contacts
     * @throws SQLException
     * @return contactsObservableList
     */
    public static ObservableList<Contact> loadAllContacts() throws SQLException {
        ObservableList<Contact> allContacts = FXCollections.observableArrayList();
        String sqlData = "SELECT * from Contacts";
        PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlData);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int contact_ID = rs.getInt("ContactID");
            String name = rs.getString("ContactName");
            String email = rs.getString("Email");
            Contact contact = new Contact(contact_ID, name, email);
            allContacts.add(contact);
        }
        return allContacts;
    }

    /**
     * Able to search for contact ID when given the contact name
     * @throws SQLException
     * @param ci
     * @return contactID
     */
    public static String contactIDSearch(String ci) throws SQLException {
        PreparedStatement ps = ConnectDB.getConnection().prepareStatement("SELECT * FROM Contacts WHERE ContactName = ?");
        ps.setString(1, ci);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ci = rs.getString("ContactID");
        }
        return ci;
    }
}
